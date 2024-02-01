import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { SessionService } from 'src/app/services/session.service';
import { of } from 'rxjs';
import { ListComponent } from './list.component';
import { Session } from '../../interfaces/session.interface';
import { SessionApiService } from '../../services/session-api.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { expect } from '@jest/globals';

class MockSessionApiService {
  all() {
    return of([
      { id: 1, name: 'Session pour les nouveaux', date: new Date(), description: 'Session pour les nouveaux' },
      { id: 2, name: 'Session pour les pros', date: new Date(), description: 'Session pour les pros' },
    ] as Session[]);
  }
}

describe('ListComponent Integration Test Suites', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionService: SessionService;
  let sessionApiService: SessionApiService;
  let router: Router;
  const sessionInfos: SessionInformation = {
    username: '',
    firstName: '',
    lastName: '',
    id: 0,
    admin: true,
    token: '',
    type: '',
  };

  beforeEach(async () => {
    // Configuration de TestBed et initialisation des variables
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        { provide: SessionService },
        { provide: SessionApiService, useClass: MockSessionApiService }
      ]
    })
      .compileComponents();
    sessionService = TestBed.inject(SessionService);
    sessionApiService = TestBed.inject(SessionApiService);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionService.sessionInformation = sessionInfos;
    fixture.detectChanges();
  });

  it('should display sessions and buttons Create and Edit for Admin User', () => {
    // Interaction : Afficher des sessions en tant qu'administrateur
    sessionService.sessionInformation!.admin = true;
    fixture.detectChanges();
    
    // Vérifications : S'assurer que les éléments sont correctement affichés
    const sessionElements = fixture.nativeElement.querySelectorAll('.item');
    const createButton = fixture.nativeElement.querySelector('button[routerLink="create"]');
    const editButton = fixture.nativeElement.querySelector('[ng-reflect-router-link="update,1"]');
    expect(sessionElements.length).toBe(2); // Vérifie qu'il y a deux sessions affichées
    expect(createButton).toBeTruthy(); // Vérifie que le bouton Create est présent
    expect(editButton).toBeTruthy(); // Vérifie que le bouton Edit est présent pour la première session
  });

  it('should display sessions and not buttons Create and Edit for User', () => {
    // Interaction : Afficher des sessions en tant qu'utilisateur non administrateur
    sessionService.sessionInformation!.admin = false;
    fixture.detectChanges();
    
    // Vérifications : S'assurer que les éléments sont correctement affichés
    const sessionElements = fixture.nativeElement.querySelectorAll('.item');
    const createButton = fixture.nativeElement.querySelector('button[routerLink="create"]');
    const editButton = fixture.nativeElement.querySelector('[ng-reflect-router-link="update,1"]');
    expect(sessionElements.length).toBe(2); // Vérifie qu'il y a deux sessions affichées
    expect(createButton).toBeFalsy(); // Vérifie que le bouton Create n'est pas présent
    expect(editButton).toBeFalsy(); // Vérifie que le bouton Edit n'est pas présent
  });
});