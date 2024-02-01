import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';
import { ActivatedRoute, convertToParamMap, Router } from "@angular/router";
import { of } from "rxjs";
import { Session } from "../../interfaces/session.interface";

describe('FormComponent Test Suites', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: SessionApiService;
  let sessionService: SessionService;
  let matSnackBar: MatSnackBar;
  let router: Router;
  let session: Session;

  const mockSessionService = {
    sessionInformation: {
      admin: false
    }
  }

  class MockSnackBar {
    open() {
      return {
        onAction: () => of({}),
      };
    }
  }

  class MockRouter {
    get url(): string {
      return '';
    }

    navigate(): Promise<boolean> {
      return new Promise<boolean>((resolve, _) => resolve(true));
    }
  }

  class MockRouterUpdate extends MockRouter {
    override get url(): string {
      return 'update';
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: Router, useClass: MockRouter },
        { provide: SessionService, useValue: mockSessionService },
        { provide: MatSnackBar, useClass: MockSnackBar },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } } },
        SessionApiService,
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService);
    sessionService = TestBed.inject(SessionService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);
    session = {
      name: 'Séance Découverte',
      description: 'Session pour les débutants',
      date: new Date('2023-12-01'),
      teacher_id: 1,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };
  });

  describe('When Init/Create', () => {
    beforeEach(() => {
        jest.spyOn(router, 'url', 'get').mockReturnValue('');
        component.onUpdate = false;
    });
  
    it('should call submit form to create session', () => {
      // Simule la réponse du service
      const spySessionApiService = jest.spyOn(sessionApiService, 'create').mockReturnValue(of(session));
      const spyMatSnackBar = jest.spyOn(matSnackBar, 'open');
      const spyRouter = jest.spyOn(router, 'navigate');
  
      component.ngOnInit();
      // Rempli le formulaire avec des données valides
      component.sessionForm?.setValue({
        name: 'New Session',
        date: new Date(),
        teacher_id: 2,
        description: 'New session description'
      });
      // Soumettre le formulaire
      component.submit();
  
      // Vérifie que le service de création est appelé
      expect(spySessionApiService).toHaveBeenCalledWith({
        name: 'New Session',
        date: expect.any(Date),
        teacher_id: 2,
        description: 'New session description'
      });
  
      // Vérifie que le Snackbar est affiché
      expect(spyMatSnackBar).toHaveBeenCalledWith('Session created !', 'Close', {duration: 3000});
  
      // Vérifie que la navigation est déclenchée vers la liste des sessions
      expect(spyRouter).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('When Update', () => {
    beforeEach(() => {
        jest.spyOn(router, 'url', 'get').mockReturnValue('update');
        component.onUpdate = true;
  
        // Simule la réponse de détail du service
        jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(session));
    });
  
    it('should call submit form to update session', () => {
      // Simule la réponse du service de mise à jour
      const spySessionApiServiceUpdate = jest.spyOn(sessionApiService, 'update').mockReturnValue(of(session));
      const spyMatSnackBar = jest.spyOn(matSnackBar, 'open');
      const spyRouter = jest.spyOn(router, 'navigate');
  
      component.ngOnInit();
      fixture.detectChanges();
  
      // Modifie les données dans le formulaire
      component.sessionForm?.patchValue({
        name: 'Updated Session',
        date: new Date(),
        teacher_id: 2,
        description: 'Updated session description'
      });
      // Soumettre le formulaire
      component.submit();
  
      // Vérifie que le service de mise à jour est appelé
      expect(spySessionApiServiceUpdate).toHaveBeenCalledWith('1', {
        name: 'Updated Session',
        date: expect.any(Date),
        teacher_id: 2,
        description: 'Updated session description'
      });
  
      // Vérifie que le Snackbar est affiché
      expect(spyMatSnackBar).toHaveBeenCalledWith('Session updated !', 'Close', {duration: 3000});
  
      // Vérifie que la navigation est déclenchée vers la liste des sessions
      expect(spyRouter).toHaveBeenCalledWith(['sessions']);
    });
  });
});