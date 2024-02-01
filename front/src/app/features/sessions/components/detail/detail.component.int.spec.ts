import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';
import { DetailComponent } from './detail.component';
import { SessionApiService } from "../../services/session-api.service";
import { of } from "rxjs";
import { TeacherService } from "../../../../services/teacher.service";

describe('DetailComponent Integration Tests', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: SessionApiService;
  let matSnackBar: MatSnackBar;

  const mockSessionInfo = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionInfo },
        SessionApiService,
        TeacherService
      ],
    }).compileComponents();
  
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    sessionApiService = TestBed.inject(SessionApiService);
    matSnackBar = TestBed.inject(MatSnackBar);
  });

  it('successfully deletes a session and shows a confirmation message', () => {
    const deleteSpy = jest.spyOn(sessionApiService, 'delete').mockReturnValue(of(null));
    const snackBarSpy = jest.spyOn(matSnackBar, 'open');
  
    component.delete();
  
    expect(deleteSpy).toHaveBeenCalledTimes(1);
    // Assurez-vous que ce message correspond exactement à celui utilisé dans votre composant
    expect(snackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', {duration: 3000});
  });
});