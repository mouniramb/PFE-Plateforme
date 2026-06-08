import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-apprenant-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './apprenant-layout.component.html',
  styleUrls: ['./apprenant-layout.component.scss']
})
export class ApprenantLayoutComponent {
  get currentUser() { return this.authService.getCurrentUser(); }

  constructor(private authService: AuthService, private router: Router) {}

  isLoggedIn(): boolean  { return this.authService.isLoggedIn(); }
  isApprenant(): boolean { return this.authService.isApprenant(); }
  isAdmin(): boolean     { return this.authService.isAdmin(); }
  isFormateur(): boolean { return this.authService.isFormateur(); }

  logout(): void    { this.authService.logout(); }
  goToLogin(): void { this.router.navigate(['/login']); }
}
