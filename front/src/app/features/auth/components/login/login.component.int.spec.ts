import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from "@angular/router";
import { RouterTestingModule } from '@angular/router/testing';
import { expect, jest } from '@jest/globals';
import { of, throwError } from "rxjs";
import { SessionService } from 'src/app/services/session.service';
import { SessionInformation } from "../../../../interfaces/sessionInformation.interface";
import { AuthService } from '../../services/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent Integration Tests', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        SessionService,
        AuthService,
        {
          provide: Router,
          useValue: {
            navigate: jest.fn(),
          },
        }
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        HttpClientTestingModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should successfully call login and navigate when credentials are valid', () => {
    // Mock du retour d'authService et sessionService
    const mockSessionInfo: SessionInformation = {
      token: 'mockToken',
      type: 'mockType',
      id: 1,
      username: 'toto@gmail.com',
      firstName: 'toto',
      lastName: 'tutu',
      admin: true
    };
    jest.spyOn(authService, 'login').mockReturnValue(of(mockSessionInfo));
    jest.spyOn(sessionService, 'logIn');
    jest.spyOn(router, 'navigate');

    // Remplissage et soumission du formulaire
    component.form.setValue({ email: 'toto@gmail.com', password: 'test!1234' });
    component.submit();

    // Assertions
    expect(component.form.valid).toBe(true);
    expect(authService.login).toHaveBeenCalled();
    expect(sessionService.logIn).toHaveBeenCalledWith(mockSessionInfo);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('should handle errors when login fails due to invalid credentials', () => {
    // Mock de l'échec de authService
    jest.spyOn(authService, 'login').mockReturnValue(throwError(() => new Error('Login Failed')));

    // Remplissage du formulaire avec des informations incorrectes
    component.form.setValue({ email: 'invalidUser@example.com', password: 'invalidPassword' });
    component.submit();

    // Assertions
    expect(component.form.valid).toBe(true);
    expect(authService.login).toHaveBeenCalled();
    expect(component.onError).toBe(true);
  });
});