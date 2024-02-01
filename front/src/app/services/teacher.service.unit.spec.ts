import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { Teacher } from "../interfaces/teacher.interface";

describe('TeacherService Test Suites', () => {
  let teacherService: TeacherService;
  let httpTestingController: HttpTestingController;
  const pathService = 'api/teacher';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    httpTestingController = TestBed.inject(HttpTestingController);
    teacherService = TestBed.inject(TeacherService);
  });

  it('should be created', () => {
    // Le service doit être créé avec succès
    expect(teacherService).toBeTruthy();
  });

  it('should get all teachers successfully', () => {
    const expectedTeachers: Teacher[] = [
      { id: 1, lastName: 'tutu', firstName: 'tutu', createdAt: new Date(), updatedAt: new Date() },
      { id: 2, lastName: 'tyty', firstName: 'tyty', createdAt: new Date(), updatedAt: new Date() }
    ];

    teacherService.all().subscribe(
      (teachersReturned) => {
        // Les enseignants retournés doivent correspondre à ceux attendus
        expect(teachersReturned).toEqual(expectedTeachers);
      }
    );

    // Vérification de la requête HTTP
    const req = httpTestingController.expectOne(pathService);
    expect(req.request.method).toEqual('GET');
    req.flush(expectedTeachers);
    httpTestingController.verify();
  });

  it('should get the details of a teacher by Id successfully', () => {
    const mockIdTeacher: string = '1';
    const expectedTeacher: Teacher = {
      id: 1,
      lastName: 'tutu',
      firstName: 'tutu',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    teacherService.detail(mockIdTeacher).subscribe(
      (teacherReturned) => {
        // L'enseignant retourné doit correspondre à celui attendu
        expect(teacherReturned).toEqual(expectedTeacher);
      }
    );

    // Vérification de la requête HTTP
    const req = httpTestingController.expectOne(`${pathService}/${mockIdTeacher}`);
    expect(req.request.method).toEqual('GET');
    req.flush(expectedTeacher);
    httpTestingController.verify();
  });
});