import {HttpClientModule} from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {expect, jest} from '@jest/globals';
import {RegisterComponent} from './register.component';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";
import {of, throwError} from "rxjs";

describe('RegisterComponent Integration Tests', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;
  let formData: { email: string, firstName: string, lastName: string, password: string };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
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
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    formData = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      password: 'John123!'
    };
  });

  it('should submit form data successfully and navigate to login', () => {
    component.form.setValue(formData);
    const spyRegister = jest.spyOn(authService, 'register').mockReturnValue(of(undefined));
    const spyNavigate = jest.spyOn(router, 'navigate');

    expect(component.form.valid).toBe(true);
    component.submit();

    expect(spyRegister).toHaveBeenCalledWith(formData);
    expect(spyNavigate).toHaveBeenCalledWith(['/login']);
  });

  it('should handle form submission error when email is already registered', () => {
    const errorData = { ...formData, email: 'existing.email@example.com' };
    component.form.setValue(errorData);
    const spyRegister = jest.spyOn(authService, 'register').mockReturnValue(throwError(() => new Error('Email already registered')));

    component.submit();

    expect(component.form.valid).toBe(true);
    expect(spyRegister).toHaveBeenCalledWith(errorData);
    expect(component.onError).toBe(true);
  });
});