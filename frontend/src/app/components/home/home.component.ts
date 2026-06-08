import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {

  constructor(public authService: AuthService, private router: Router) {}

  goToDashboard(): void {
    const role = this.authService.getUserRole();
    if (role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else if (role === 'FORMATEUR') {
      this.router.navigate(['/formateur/dashboard']);
    } else if (role === 'APPRENANT') {
      this.router.navigate(['/catalogue']);
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
