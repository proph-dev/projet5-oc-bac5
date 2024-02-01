import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { SessionInformation } from '../../interfaces/sessionInformation.interface';
import { SessionService } from '../../services/session.service';
import { UserService } from '../../services/user.service';
import { MeComponent } from './me.component';
import { jest, expect } from '@jest/globals';
import { HttpClientModule } from '@angular/common/http';

describe('MeComponent Test Suites', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  let matSnackBar: MatSnackBar;
  let sessionService: SessionService;
  let router: Router;

  const mockSessionInformation: SessionInformation = {
    token: 'bearer token',
    type: 'jwt',
    id: 1,
    username: 'toto@gmail.com',
    firstName: 'toto',
    lastName: 'toto',
    admin: false,
  };

  const mockSessionService = {
    sessionInformation: mockSessionInformation,
    logOut: jest.fn(),
  };

  class MockSnackBar {
    open() {
      return {
        onAction: () => of({}),
      };
    }
  }

  class MockRouter {
    get url(): string {
      return 'update';
    }

    navigate(): Promise<boolean> {
      return new Promise<boolean>((resolve) => resolve(true));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        RouterTestingModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        HttpClientModule,
      ],
      providers: [
        UserService,
        { provide: Router, useClass: MockRouter },
        { provide: SessionService, useValue: mockSessionService },
        { provide: MatSnackBar, useClass: MockSnackBar },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    sessionService = TestBed.inject(SessionService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should delete a user', () => {
    const spyUserServiceDelete = jest.spyOn(userService, 'delete').mockReturnValue(of({}));
    const spyMatSnackBar = jest.spyOn(matSnackBar, 'open');
    const spySessionServiceLogout = jest.spyOn(sessionService, 'logOut');
    const spyRouter = jest.spyOn(router, 'navigate');
    
    component.delete();

    expect(spyUserServiceDelete).toHaveBeenCalled();
    expect(spyMatSnackBar).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(spySessionServiceLogout).toHaveBeenCalled();
    expect(spyRouter).toHaveBeenCalledWith(['/']);
  });
});