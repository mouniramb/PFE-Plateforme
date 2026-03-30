import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {

  currentUser = this.authService.getCurrentUser();
  formateursCount = 0;
  isLoading = true;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.userService.getAllFormateurs().subscribe({
      next: (data) => {
        this.formateursCount = data.length;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = mapHttpErrorMessage(err, 'Erreur lors du chargement des statistiques.');
        this.isLoading = false;
      }
    });
  }

  retryLoadingStats(): void {
    this.loadStats();
  }

  logout(): void {
    this.authService.logout();
  }
}
