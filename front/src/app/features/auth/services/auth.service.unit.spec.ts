import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { AuthService } from './auth.service';
import { RegisterRequest } from "../interfaces/registerRequest.interface";
import { LoginRequest } from "../interfaces/loginRequest.interface";
import { SessionInformation } from "../../../interfaces/sessionInformation.interface";

describe('AuthService Tests', () => {
  let authService: AuthService;
  let httpTestingController: HttpTestingController;
  const apiEndpoint = 'api/auth';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    authService = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('can be instantiated', () => {
    expect(authService).toBeDefined();
  });

  it('executes register operation', () => {
    const testRegisterRequest: RegisterRequest = {
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'Password123'
    };
    authService.register(testRegisterRequest).subscribe();

    const req = httpTestingController.expectOne(apiEndpoint + '/register');
    expect(req.request.method).toBe('POST');
    req.flush(null);
    httpTestingController.verify();
  });

  it('executes login operation and returns session info', () => {
    const testLoginRequest: LoginRequest = {
      email: 'user@example.com',
      password: 'UserPassword'
    };
    const mockSessionInfo: SessionInformation = {
      id: 2,
      username: 'user@example.com',
      firstName: 'User',
      lastName: 'Example',
      token: 'bearerToken',
      type: 'jwt',
      admin: true,
    };
    authService.login(testLoginRequest).subscribe(
      data => expect(data).toEqual(mockSessionInfo)
    );

    const req = httpTestingController.expectOne(apiEndpoint + '/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockSessionInfo);
    httpTestingController.verify();
  });
});