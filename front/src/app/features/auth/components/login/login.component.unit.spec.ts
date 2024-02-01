import {HttpClientModule} from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule, FormGroup} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {expect, jest} from '@jest/globals';
import {LoginComponent} from './login.component';
import {SessionService} from "../../../../services/session.service";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

describe('LoginComponent Test Suites', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let sessionService: SessionService;
  let authService: AuthService;
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
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should have invalid form when empty', () => {
    const form: FormGroup = component.form;
    form.controls['email'].setValue('');
    form.controls['password'].setValue('');
    expect(form.invalid).toBe(true);
  });

  it('should have invalid form with incorrect data', () => {
    const form: FormGroup = component.form;
    form.controls['email'].setValue('invalid-email');
    form.controls['password'].setValue('123');
    expect(form.invalid).toBe(true);
  });

  it('should have a valid form with correct data', () => {
    component.form.controls['email'].setValue('toto@yoga.fr');
    component.form.controls['password'].setValue('test!1234');
    expect(component.form.valid).toBe(true);
  });

});