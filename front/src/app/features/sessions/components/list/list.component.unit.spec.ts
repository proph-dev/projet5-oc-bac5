import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';

describe('ListComponent Test Suites', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  // Utilisation de beforeEach pour configurer le composant et le service de session
  beforeEach(async () => {
    const mockSessionService = {
      sessionInformation: {
        admin: true
      }
    };

    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule],
      providers: [{ provide: SessionService, useValue: mockSessionService }]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test de création du composant
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test pour vérifier si le composant obtient les informations sur l'utilisateur
  it('should get user information', () => {
    expect(component.user?.admin).toBe(true);
  });
});