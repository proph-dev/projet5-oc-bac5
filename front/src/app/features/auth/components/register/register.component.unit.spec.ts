import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect, jest } from '@jest/globals';
import { RegisterComponent } from './register.component';
import { RouterTestingModule } from "@angular/router/testing";
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";

describe('RegisterComponent Tests', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

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
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('component should be instantiated', () => {
    expect(component).toBeDefined();
  });

  it('form should be invalid with an improperly formatted email', () => {
    const testData = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'incorrect-email-format',
      password: 'Password123!'
    };
    component.form.setValue(testData);
    expect(component.form.invalid).toBe(true);
  });

  it('form should be valid with properly filled fields', () => {
    const validData = {
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane.smith@example.com',
      password: 'StrongPassword!2'
    };
    component.form.setValue(validData);
    expect(component.form.valid).toBe(true);
  });
});