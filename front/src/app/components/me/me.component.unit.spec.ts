import {HttpClientModule} from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {SessionService} from '../../services/session.service';
import {UserService} from '../../services/user.service';
import {MeComponent} from './me.component';
import {jest, expect} from "@jest/globals";
import {RouterTestingModule} from "@angular/router/testing";
import { Router } from '@angular/router';
import { fakeAsync, tick } from '@angular/core/testing';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { flush } from '@angular/core/testing';

describe('MeComponent Test Suites', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  let sessionService: SessionService;
  let matSnackBar: MatSnackBar;
  let router: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: false,
      id: 2
    },
    logOut: jest.fn()
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        RouterTestingModule,
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        NoopAnimationsModule
      ],
      providers: [
        UserService,
        {provide: SessionService, useValue: mockSessionService}
      ]
    })
    .compileComponents();

    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    sessionService = TestBed.inject(SessionService);
    fixture.detectChanges();
  });

  it('should create and initialize properties', () => {
    expect(component).toBeTruthy();
    expect(component.user).toBeUndefined();
  });

  it('should call delete and navigate on delete', fakeAsync(() => {
    jest.spyOn(userService, 'delete').mockReturnValue(of(null));
    const spyUserService = jest.spyOn(userService, 'delete');
    const spyMatSnackBar = jest.spyOn(matSnackBar, 'open');
    const spyRouter = jest.spyOn(router, 'navigate');
    
    component.delete();
    tick();
    flush();
  
    expect(spyUserService).toHaveBeenCalled();
    expect(spyMatSnackBar).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
    expect(spyRouter).toHaveBeenCalledWith(['/',]);
  }));

  it('should call userService.getById ngOnInit', () => {
    const spyUserService = jest.spyOn(userService, 'getById');
    component.ngOnInit();
    expect(spyUserService).toHaveBeenCalled();
  });


  it('should navigate back', () => {
    const spyWindow = jest.spyOn(window.history, 'back');
    component.back();
    expect(spyWindow).toHaveBeenCalled();
  })
});