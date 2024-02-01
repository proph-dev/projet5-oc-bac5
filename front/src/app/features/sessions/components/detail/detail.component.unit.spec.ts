import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';
import { DetailComponent } from './detail.component';
import { SessionApiService } from "../../services/session-api.service";

describe('DetailComponent Tests', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: SessionApiService;

  const testSessionService = {
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
      providers: [{ provide: SessionService, useValue: testSessionService }],
    }).compileComponents();
  
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    sessionApiService = TestBed.inject(SessionApiService);
  });

  it('component should be successfully created', () => {
    expect(component).toBeDefined();
  });

  it('navigates back when back function is called', () => {
    const historySpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historySpy).toHaveBeenCalledTimes(1);
  });

  it('calls participate function on session service when participate is invoked', () => {
    const participateSpy = jest.spyOn(sessionApiService, 'participate');
    component.participate();
    expect(participateSpy).toBeCalled();
  });

  it('calls unparticipate function on session service when unparticipate is invoked', () => {
    const unparticipateSpy = jest.spyOn(sessionApiService, 'unParticipate');
    component.unParticipate();
    expect(unparticipateSpy).toBeCalled();
  });
});