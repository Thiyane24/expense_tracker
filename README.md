# ExpenseTracker | Minimalist Financial Studio

ExpenseTracker is a high-end, minimalist personal finance management application designed for those who value clarity, speed, and a clutter-free experience. Built with a "white-sheet" design philosophy, it strips away corporate noise to focus entirely on the data that matters: your money.

![Version](https://img.shields.io/badge/version-1.0.0-stone)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring](https://img.shields.io/badge/Spring_Boot-3.3.0-green)
![Database](https://img.shields.io/badge/PostgreSQL-Supabase-blue)

## Design Philosophy
The application follows a Humanized Minimalist approach:
- Studio Aesthetic: White backgrounds, a monochromatic palette (Stone/Black), and high-contrast typography.
- Zero Noise: No generic templates, no heavy shadows, and no emojis.
- Fluid Experience: A Single Page Application (SPA) architecture that feels like a native desktop tool.
- Responsive: Perfectly optimized for everything from a 4K monitor to a smartphone.

## Features

### Authentication and Security
- Secure User Accounts: Full registration and login flow.
- Token-Based Sessions: Persistent authentication using tokens stored in localStorage.
- Private Data: Each user has their own isolated financial environment.

### Financial Management
- Dynamic Budgeting: Set and update your monthly spending limit in real-time.
- Instant Entry: A streamlined "Quick Add" form for recording expenses in seconds.
- Detailed History: A clean, chronological list of all transactions with category tagging.
- Live Search: Filter through hundreds of transactions instantly using the integrated search bar.
- Currency Flexibility: Seamlessly toggle between MYR (Ringgit) and MZN (Metical).

## Tech Stack

### Backend
- Language: Java 21
- Framework: Spring Boot 3.3.0
- Database: PostgreSQL (via Supabase)
- Migration: Flyway (for versioned schema management)
- Build Tool: Maven

### Frontend
- UI: Vanilla JavaScript (SPA)
- Styling: Tailwind CSS (CDN)
- Typography: Inter (Google Fonts)
- Architecture: Client-side state management for instant UI updates.

## Installation and Setup

### Backend
1. Clone the repo:
   ```bash
   git clone https://github.com/yourusername/expense-tracker.git
   cd expense-tracker
   ```
2. Configure Environment:
   Create an application-dev.yml in src/main/resources/ or set the following environment variables:
   - SPRING_DATASOURCE_URL
   - SPRING_DATASOURCE_USERNAME
   - SPRING_DATASOURCE_PASSWORD
3. Run the App:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend
Since the frontend is a single index.html file, you can:
- Open it directly in any modern browser.
- Deploy it to GitHub Pages or Vercel.
- Ensure the API_BASE_URL in the script section is updated to point to your deployed backend.

## API Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| POST | /api/auth/signup | Create a new user account | No |
| POST | /api/auth/login | Authenticate user and get token | No |
| PATCH | /api/auth/budget | Update monthly budget limit | Yes |
| GET | /api/transactions | List all user transactions | Yes |
| POST | /api/transactions | Register a new expense | Yes |
| DELETE | /api/transactions/{id} | Remove a transaction | Yes |

---
Designed for those who prefer the beauty of simplicity.
