                                                                                                                                                        # Smart Parking System (SPS) — Project Documentation

                                                                                                                                                        ---

                                                                                                                                                        ## 1. Cover Page

                                                                                                                                                        | Field | Details |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Project Title** | Smart Parking System (SPS) |
                                                                                                                                                        | **Team Name** | *(Your Team Name)* |
                                                                                                                                                        | **Submission Date** | April 18, 2026 |

                                                                                                                                                        ### Team Members & Roles

                                                                                                                                                        | Name | Role |
                                                                                                                                                        |---|---|
                                                                                                                                                        | *(Member 1)* | Full Stack Developer — Backend (Spring Boot Microservices) |
                                                                                                                                                        | *(Member 2)* | Full Stack Developer — Frontend (Angular 19) |
                                                                                                                                                        | *(Member 3)* | Database Designer & API Integration |
                                                                                                                                                        | *(Member 4)* | UI/UX Designer & Testing |

                                                                                                                                                        ### Tech Stack Summary

                                                                                                                                                        | Layer | Technology |
                                                                                                                                                        |---|---|
                                                                                                                                                        | Frontend | Angular 19 (Standalone Components, SSR disabled) |
                                                                                                                                                        | Backend | Spring Boot 3 (Java 21, Microservices) |
                                                                                                                                                        | Database | MySQL / PostgreSQL (JPA + Hibernate) |
                                                                                                                                                        | Auth | JWT (JSON Web Tokens) with Role-based Access Control |
                                                                                                                                                        | API Docs | Swagger / OpenAPI 3 |
                                                                                                                                                        | Build | Maven (Backend), Angular CLI / esbuild (Frontend) |

                                                                                                                                                        ---

                                                                                                                                                        ## 2. Executive Summary

                                                                                                                                                        ### Overview
                                                                                                                                                        The **Smart Parking System (SPS)** is a full-stack web application that digitizes and streamlines urban parking management. It connects two Spring Boot microservices with an Angular frontend to allow users to find, book, and manage parking slots — and allows administrators to oversee the entire parking ecosystem in real time.

                                                                                                                                                        ### Problem
                                                                                                                                                        Urban areas suffer from inefficient parking — drivers waste time and fuel searching for slots, parking lots operate without visibility into occupancy, and there is no unified digital interface for managing vehicle–slot–booking lifecycles.

                                                                                                                                                        ### Solution
                                                                                                                                                        SPS provides:
                                                                                                                                                        - A **user-facing portal** for vehicle registration, building discovery, slot booking, and booking history.
                                                                                                                                                        - An **admin panel** with live dashboards, occupancy tracking, user management, and slot lifecycle control.

                                                                                                                                                        ### User Impact
                                                                                                                                                        - Users save time with instant slot availability and mobile-friendly booking.
                                                                                                                                                        - Admins gain full operational visibility with no manual tracking.

                                                                                                                                                        ### Innovation Summary
                                                                                                                                                        - Microservice architecture with inter-service vehicle validation at booking time.
                                                                                                                                                        - Real-time slot occupancy tiles with live status.
                                                                                                                                                        - JWT-based role separation (USER / ADMIN) enforced at both backend and frontend layers.

                                                                                                                                                        ---

                                                                                                                                                        ## 3. Problem Statement

                                                                                                                                                        ### Real-World Challenge
                                                                                                                                                        Urban parking is largely undigitized — most parking lots still rely on manual entry logs, paper tokens, or standalone barriers with no integration into a booking or management system. This results in:
                                                                                                                                                        - **Wasted time**: Drivers circling lots looking for available spaces.
                                                                                                                                                        - **No advance booking**: No way to reserve a slot before arriving.
                                                                                                                                                        - **Operational blind spots**: Parking operators have no live dashboard of slot occupancy.
                                                                                                                                                        - **No vehicle-type awareness**: CAR and BIKE slots are managed interchangeably, causing mismatches.

                                                                                                                                                        ### Who Is Affected
                                                                                                                                                        - **Daily commuters** who park near offices or transit hubs.
                                                                                                                                                        - **Parking lot operators** managing multi-building complexes.
                                                                                                                                                        - **City administrators** trying to reduce traffic congestion from parking searches.

                                                                                                                                                        ### Evidence / Examples
                                                                                                                                                        - Studies show drivers spend an average of 17 minutes searching for parking in urban centers.
                                                                                                                                                        - Unmanaged lots frequently report double-occupancy disputes with no digital record.
                                                                                                                                                        - Fleet managers have no visibility into where company vehicles are parked.

                                                                                                                                                        ---

                                                                                                                                                        ## 4. Proposed Solution

                                                                                                                                                        ### Solution Description
                                                                                                                                                        SPS is a **two-microservice, one-frontend** architecture:

                                                                                                                                                        | Service | Port | Responsibility |
                                                                                                                                                        |---|---|---|
                                                                                                                                                        | `myapp` (User & Vehicle Service) | 8090 | Registration, Login, JWT, Vehicle CRUD |
                                                                                                                                                        | `parking-service` (Parking Service) | 8091 | Buildings, Slots, Bookings |
                                                                                                                                                        | Angular Frontend | 4200 | UI for users and admins |

                                                                                                                                                        ### How It Solves the Problem
                                                                                                                                                        1. Users register vehicles (CAR / BIKE) and browse available buildings.
                                                                                                                                                        2. A 4-step wizard guides the user: **Building → Vehicle → Slot → Confirm Booking**.
                                                                                                                                                        3. Slots are vehicle-type matched — CAR slots only appear for CAR vehicles.
                                                                                                                                                        4. Admins monitor live occupancy, manage slots, view all bookings by status, and manage users.
                                                                                                                                                        5. Booking lifecycle: **ACTIVE → COMPLETED (EXIT)** or **ACTIVE → CANCELLED**.

                                                                                                                                                        ### Unique Value & Innovation
                                                                                                                                                        - **Cross-service vehicle validation**: When a booking is created, `parking-service` calls `myapp` via WebClient to validate vehicle ownership before confirming.
                                                                                                                                                        - **Role-based UI separation**: Admin and User see completely different navigation and pages — enforced at route and API level.
                                                                                                                                                        - **Live slot occupancy tiles**: Admin Slots page shows real-time AVAILABLE/OCCUPIED status per building with color-coded tiles.
                                                                                                                                                        - **Active booking guard on slot deletion**: A slot cannot be deleted if it has an active booking — enforced at both client and server.

                                                                                                                                                        ---

                                                                                                                                                        ## 5. Use Cases

                                                                                                                                                        ---

                                                                                                                                                        ### UC-01: User Registration & Login

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | New User |
                                                                                                                                                        | **Preconditions** | User has a valid email address |
                                                                                                                                                        | **Workflow** | 1. Navigate to `/auth/register` → 2. Enter Name, Email, Password → 3. Submit → 4. JWT issued → 5. Redirected to Dashboard |
                                                                                                                                                        | **Success Criteria** | User is logged in, JWT stored, Dashboard visible |

                                                                                                                                                        ---

                                                                                                                                                        ### UC-02: Register a Vehicle

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | Logged-in User |
                                                                                                                                                        | **Preconditions** | User is authenticated |
                                                                                                                                                        | **Workflow** | 1. Navigate to Vehicles → 2. Click Add Vehicle → 3. Enter vehicle number, type (CAR/BIKE), model → 4. Submit → 5. Vehicle appears in list |
                                                                                                                                                        | **Success Criteria** | Vehicle saved and listed; available for booking |

                                                                                                                                                        ---

                                                                                                                                                        ### UC-03: Book a Parking Slot

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | Logged-in User with at least one registered vehicle |
                                                                                                                                                        | **Preconditions** | At least one building with available matching slots exists; user has no active booking |
                                                                                                                                                        | **Workflow** | 1. Navigate to Bookings → New Booking → 2. Select Building → 3. Select Vehicle → 4. Available slots filtered by vehicle type appear → 5. Select Slot → 6. Confirm → 7. Booking created with status ACTIVE |
                                                                                                                                                        | **Success Criteria** | Booking created, slot status changes to OCCUPIED |

                                                                                                                                                        ---

                                                                                                                                                        ### UC-04: Complete (Exit) a Booking

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | Logged-in User with ACTIVE booking |
                                                                                                                                                        | **Preconditions** | User has an active booking |
                                                                                                                                                        | **Workflow** | 1. Navigate to Bookings → Active tab → 2. Click "Exit / Complete" → 3. Confirmation → 4. Booking status set to COMPLETED, endTime recorded, slot released |
                                                                                                                                                        | **Success Criteria** | Booking moves to Completed tab, slot becomes AVAILABLE |

                                                                                                                                                        ---

                                                                                                                                                        ### UC-05: Cancel a Booking

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | Logged-in User with ACTIVE booking |
                                                                                                                                                        | **Preconditions** | User has an active booking |
                                                                                                                                                        | **Workflow** | 1. Navigate to Bookings → Active tab → 2. Click "Cancel" → 3. Booking status set to CANCELLED, cancellation time recorded, slot released |
                                                                                                                                                        | **Success Criteria** | Booking moves to Cancelled tab with cancellation timestamp |

                                                                                                                                                        ---

                                                                                                                                                        ### UC-06: Admin — Manage Slots

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | Admin user |
                                                                                                                                                        | **Preconditions** | Admin is logged in |
                                                                                                                                                        | **Workflow** | 1. Navigate to Admin → Slots → 2. All buildings shown with slot tiles → 3. Admin can add a new slot (slotNumber + vehicleType) → 4. Admin can delete an AVAILABLE slot → 5. Attempt to delete OCCUPIED slot shows error |
                                                                                                                                                        | **Success Criteria** | Slot created/deleted; live tile updates |

                                                                                                                                                        ---

                                                                                                                                                        ### UC-07: Admin — View & Filter Bookings

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | Admin user |
                                                                                                                                                        | **Preconditions** | Admin is logged in |
                                                                                                                                                        | **Workflow** | 1. Navigate to Admin → Bookings → 2. Default ALL tab loads all bookings → 3. Click ACTIVE / COMPLETED / CANCELLED tab to filter → 4. Each row shows user, vehicle, slot, building, duration, timestamps |
                                                                                                                                                        | **Success Criteria** | Bookings filtered by status with correct data |

                                                                                                                                                        ---

                                                                                                                                                        ### UC-08: Edit User Profile

                                                                                                                                                        | Field | Detail |
                                                                                                                                                        |---|---|
                                                                                                                                                        | **Actors** | Any logged-in user |
                                                                                                                                                        | **Preconditions** | User is authenticated |
                                                                                                                                                        | **Workflow** | 1. Click username dropdown → Edit Profile → 2. Update Name / Email / Password → 3. Save → 4. Navbar name updates immediately |
                                                                                                                                                        | **Success Criteria** | Profile saved, JWT session reflects new name |

                                                                                                                                                        ---

                                                                                                                                                        ## 6. Architecture & Design

                                                                                                                                                        ### High-Level Architecture

                                                                                                                                                        ```
                                                                                                                                                        ┌─────────────────────────────────────────────────────────┐
                                                                                                                                                        │                    Angular 19 Frontend                   │
                                                                                                                                                        │              (SPA — port 4200)                          │
                                                                                                                                                        │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
                                                                                                                                                        │  │  Auth    │ │ Vehicles │ │ Bookings │ │  Admin   │  │
                                                                                                                                                        │  │ Module   │ │  Module  │ │  Module  │ │  Panel   │  │
                                                                                                                                                        │  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘  │
                                                                                                                                                        │       │             │            │              │        │
                                                                                                                                                        │       └─────────────┴──── HTTP ──┴──────────────┘        │
                                                                                                                                                        └───────────────────────────────────────────────────────────┘
                                                                                                                                                                │                              │
                                                                                                                                                                ▼                              ▼
                                                                                                                                                        ┌─────────────────┐            ┌──────────────────────┐
                                                                                                                                                        │  myapp (8090)   │            │ parking-service(8091) │
                                                                                                                                                        │  Spring Boot    │◄──WebClient│  Spring Boot          │
                                                                                                                                                        │                 │────────────│                       │
                                                                                                                                                        │  • Auth/JWT     │            │  • Buildings          │
                                                                                                                                                        │  • Users        │            │  • Slots              │
                                                                                                                                                        │  • Vehicles     │            │  • Bookings           │
                                                                                                                                                        └────────┬────────┘            └──────────┬────────────┘
                                                                                                                                                                │                               │
                                                                                                                                                                ▼                               ▼
                                                                                                                                                        ┌─────────────────────────────────────────────────────────┐
                                                                                                                                                        │                        MySQL Database                    │
                                                                                                                                                        │   users | vehicles | user_vehicles                      │
                                                                                                                                                        │   buildings | slots | bookings                          │
                                                                                                                                                        └─────────────────────────────────────────────────────────┘
                                                                                                                                                        ```

                                                                                                                                                        ### Booking Workflow / Sequence Diagram

                                                                                                                                                        ```
                                                                                                                                                        User          Angular          parking-service       myapp
                                                                                                                                                        │                │                   │                 │
                                                                                                                                                        │─ Select Slot ─►│                   │                 │
                                                                                                                                                        │                │── POST /bookings ─►│                 │
                                                                                                                                                        │                │                   │─ GET /vehicles/{id} ─►│
                                                                                                                                                        │                │                   │◄── Vehicle OK ──│
                                                                                                                                                        │                │                   │                 │
                                                                                                                                                        │                │                   │── Validate slot AVAILABLE
                                                                                                                                                        │                │                   │── Check vehicle type match
                                                                                                                                                        │                │                   │── Check no existing ACTIVE booking
                                                                                                                                                        │                │                   │── Set slot OCCUPIED
                                                                                                                                                        │                │                   │── Save Booking (ACTIVE)
                                                                                                                                                        │                │◄── Booking Created─│
                                                                                                                                                        │◄─ Show Success─│
                                                                                                                                                        ```

                                                                                                                                                        ### JWT Auth Flow

                                                                                                                                                        ```
                                                                                                                                                        Login → POST /uvmgmt/users/login
                                                                                                                                                            ← { appResponse: { token: "eyJ..." } }
                                                                                                                                                                │
                                                                                                                                                                ▼
                                                                                                                                                            Decode JWT payload: { sub, userId, role, name, iat, exp }
                                                                                                                                                                │
                                                                                                                                                                ▼
                                                                                                                                                            Store in localStorage → attach via AuthInterceptor on every request
                                                                                                                                                        ```

                                                                                                                                                        ### Technology Stack Details

                                                                                                                                                        | Component | Technology | Version |
                                                                                                                                                        |---|---|---|
                                                                                                                                                        | Frontend Framework | Angular | 19 |
                                                                                                                                                        | Frontend Build | esbuild (via Angular CLI) | Latest |
                                                                                                                                                        | Backend Framework | Spring Boot | 3.x |
                                                                                                                                                        | Language | Java | 21 |
                                                                                                                                                        | ORM | Spring Data JPA + Hibernate | 3.x |
                                                                                                                                                        | Security | Spring Security + JWT | 3.x |
                                                                                                                                                        | Inter-service Calls | Spring WebClient (reactive) | 3.x |
                                                                                                                                                        | Database | MySQL | 8.x |
                                                                                                                                                        | API Documentation | SpringDoc OpenAPI / Swagger UI | 2.x |
                                                                                                                                                        | HTTP Client (FE) | Angular HttpClient | 19 |
                                                                                                                                                        | CSS Framework | Bootstrap 5 + Bootstrap Icons | 5.3 |

                                                                                                                                                        ---

                                                                                                                                                        ## 7. Features & Capabilities

                                                                                                                                                        ### User Features

                                                                                                                                                        | Feature | Description | Business Value | Technical Value |
                                                                                                                                                        |---|---|---|---|
                                                                                                                                                        | **Registration & Login** | JWT-based auth with role assignment | Secure access control | Stateless token auth, no server sessions |
                                                                                                                                                        | **Vehicle Management** | Add/edit/delete CAR or BIKE vehicles | Users manage their own fleet | CRUD via REST; ownership-based access |
                                                                                                                                                        | **Building Browser** | View all parking buildings with city/address | Users discover nearby parking | Pageable REST endpoint |
                                                                                                                                                        | **4-Step Booking Wizard** | Building → Vehicle → Slot → Confirm | Guided, error-free booking flow | Chained API calls with state management |
                                                                                                                                                        | **Booking History** | View ACTIVE / COMPLETED / CANCELLED tabs with duration | Full transparency of parking history | Status-filtered queries, date-safe pipe |
                                                                                                                                                        | **Profile Edit** | Update name, email, password | Self-service account management | PUT endpoint with session refresh |

                                                                                                                                                        ### Admin Features

                                                                                                                                                        | Feature | Description | Business Value | Technical Value |
                                                                                                                                                        |---|---|---|---|
                                                                                                                                                        | **Admin Dashboard** | Stats: buildings, bookings by status, users count + recent bookings | Instant operational overview | Parallel HTTP calls, stat aggregation |
                                                                                                                                                        | **Booking Management** | Filter all bookings by ALL/ACTIVE/COMPLETED/CANCELLED | Full booking audit trail | Status-filtered API endpoint |
                                                                                                                                                        | **User Management** | List all users with expandable vehicle rows | Know which users own which vehicles | Lazy-loaded per-user vehicle fetch with caching |
                                                                                                                                                        | **Slot Management** | Live occupancy tiles per building; add/delete slots | Real-time lot management | Parallel building+slot API calls |
                                                                                                                                                        | **Create Building** | Add new parking buildings | Expand parking network | POST endpoint with form validation |
                                                                                                                                                        | **Delete Slot (with guard)** | Delete slot; blocked if ACTIVE booking exists | Prevent data integrity issues | Server + client side active booking check |

                                                                                                                                                        ---

                                                                                                                                                        ## 8. Installation Guide

                                                                                                                                                        ### System Requirements

                                                                                                                                                        | Requirement | Version |
                                                                                                                                                        |---|---|
                                                                                                                                                        | Java | 21+ |
                                                                                                                                                        | Maven | 3.9+ |
                                                                                                                                                        | Node.js | 18+ |
                                                                                                                                                        | Angular CLI | 19+ |
                                                                                                                                                        | MySQL | 8.0+ |

                                                                                                                                                        ---

                                                                                                                                                        ### Database Setup

                                                                                                                                                        ```sql
                                                                                                                                                        -- Create databases
                                                                                                                                                        CREATE DATABASE myapp_db;
                                                                                                                                                        CREATE DATABASE parking_db;

                                                                                                                                                        -- Create user (optional)
                                                                                                                                                        CREATE USER 'sps_user'@'localhost' IDENTIFIED BY 'yourpassword';
                                                                                                                                                        GRANT ALL PRIVILEGES ON myapp_db.* TO 'sps_user'@'localhost';
                                                                                                                                                        GRANT ALL PRIVILEGES ON parking_db.* TO 'sps_user'@'localhost';
                                                                                                                                                        FLUSH PRIVILEGES;
                                                                                                                                                        ```

                                                                                                                                                        Tables are auto-created by JPA (`spring.jpa.hibernate.ddl-auto=update`).

                                                                                                                                                        ---

                                                                                                                                                        ### Backend Setup — `myapp` (User & Vehicle Service)

                                                                                                                                                        ```powershell
                                                                                                                                                        # Navigate to myapp
                                                                                                                                                        cd C:\Users\ilayaraja.k\SPS\backend\myapp

                                                                                                                                                        # Configure application.properties
                                                                                                                                                        # src/main/resources/application.properties:
                                                                                                                                                        #   spring.datasource.url=jdbc:mysql://localhost:3306/myapp_db
                                                                                                                                                        #   spring.datasource.username=root
                                                                                                                                                        #   spring.datasource.password=yourpassword
                                                                                                                                                        #   server.port=8090

                                                                                                                                                        # Run
                                                                                                                                                        .\mvnw spring-boot:run
                                                                                                                                                        ```

                                                                                                                                                        ---

                                                                                                                                                        ### Backend Setup — `parking-service`

                                                                                                                                                        ```powershell
                                                                                                                                                        # Navigate to parking-service
                                                                                                                                                        cd C:\Users\ilayaraja.k\SPS\backend\parking-service

                                                                                                                                                        # Configure application.properties
                                                                                                                                                        # src/main/resources/application.properties:
                                                                                                                                                        #   spring.datasource.url=jdbc:mysql://localhost:3306/parking_db
                                                                                                                                                        #   spring.datasource.username=root
                                                                                                                                                        #   spring.datasource.password=yourpassword
                                                                                                                                                        #   server.port=8091

                                                                                                                                                        # Run
                                                                                                                                                        .\mvnw spring-boot:run
                                                                                                                                                        ```

                                                                                                                                                        ---

                                                                                                                                                        ### Frontend Setup

                                                                                                                                                        ```powershell
                                                                                                                                                        # Navigate to frontend
                                                                                                                                                        cd C:\Users\ilayaraja.k\SPS

                                                                                                                                                        # Install dependencies
                                                                                                                                                        npm install

                                                                                                                                                        # Run development server
                                                                                                                                                        npm start
                                                                                                                                                        # App runs at http://localhost:4200
                                                                                                                                                        ```

                                                                                                                                                        ---

                                                                                                                                                        ### Environment Variables / Configuration

                                                                                                                                                        | Config | Location | Value |
                                                                                                                                                        |---|---|---|
                                                                                                                                                        | `myapp` DB URL | `myapp/src/main/resources/application.properties` | `jdbc:mysql://localhost:3306/myapp_db` |
                                                                                                                                                        | `parking-service` DB URL | `parking-service/src/main/resources/application.properties` | `jdbc:mysql://localhost:3306/parking_db` |
                                                                                                                                                        | `myapp` port | `application.properties` | `8090` |
                                                                                                                                                        | `parking-service` port | `application.properties` | `8091` |
                                                                                                                                                        | Frontend API base (myapp) | `src/app/auth/services/auth.service.ts` | `http://localhost:8090/uvmgmt` |
                                                                                                                                                        | Frontend API base (parking) | component-level constants | `http://localhost:8091/pabsm` |
                                                                                                                                                        | JWT Secret | `application.properties` → `jwt.secret` | *(Set a strong secret key)* |

                                                                                                                                                        ---

                                                                                                                                                        ## 9. User Manual

                                                                                                                                                        ### Login Steps
                                                                                                                                                        1. Open `http://localhost:4200`
                                                                                                                                                        2. You are redirected to `/auth/login`
                                                                                                                                                        3. Enter your **Email** and **Password**
                                                                                                                                                        4. Click **Login**
                                                                                                                                                        5. On success → redirected to your Dashboard (User) or Admin Dashboard (Admin)

                                                                                                                                                        ---

                                                                                                                                                        ### Register Steps
                                                                                                                                                        1. On the Login page, click **Register**
                                                                                                                                                        2. Enter **Full Name**, **Email**, **Password**
                                                                                                                                                        3. Click **Register**
                                                                                                                                                        4. Automatically logged in and redirected to Dashboard

                                                                                                                                                        ---

                                                                                                                                                        ### Book a Parking Slot
                                                                                                                                                        1. Click **Bookings** in the navbar
                                                                                                                                                        2. Click **New Booking**
                                                                                                                                                        3. **Step 1** — Select a Building from the list
                                                                                                                                                        4. **Step 2** — Select one of your registered vehicles
                                                                                                                                                        5. **Step 3** — Available slots (matching your vehicle type) appear; select one
                                                                                                                                                        6. **Step 4** — Review and click **Confirm Booking**
                                                                                                                                                        7. Booking is created with status **ACTIVE**

                                                                                                                                                        ---

                                                                                                                                                        ### Exit / Complete a Booking
                                                                                                                                                        1. Go to **Bookings** → **Active** tab
                                                                                                                                                        2. Find your active booking
                                                                                                                                                        3. Click **Exit** (or Complete)
                                                                                                                                                        4. Booking moves to **Completed** tab; slot is released

                                                                                                                                                        ---

                                                                                                                                                        ### Cancel a Booking
                                                                                                                                                        1. Go to **Bookings** → **Active** tab
                                                                                                                                                        2. Click **Cancel**
                                                                                                                                                        3. Booking moves to **Cancelled** tab with cancellation timestamp

                                                                                                                                                        ---

                                                                                                                                                        ### Edit Profile
                                                                                                                                                        1. Click your **name** in the top-right navbar
                                                                                                                                                        2. Click **Edit Profile**
                                                                                                                                                        3. Update Name, Email, or Password (leave password blank to keep existing)
                                                                                                                                                        4. Click **Save Changes**
                                                                                                                                                        5. Navbar updates with new name immediately

                                                                                                                                                        ---

                                                                                                                                                        ### Admin — View Dashboard
                                                                                                                                                        1. Login as ADMIN
                                                                                                                                                        2. Automatically redirected to `/admin`
                                                                                                                                                        3. View: Total Buildings, Active Bookings, Registered Users, Booking breakdown, Recent bookings table

                                                                                                                                                        ---

                                                                                                                                                        ### Admin — Manage Slots
                                                                                                                                                        1. Navigate to **Admin → Slots**
                                                                                                                                                        2. All buildings shown with slot tiles (green = AVAILABLE, red = OCCUPIED)
                                                                                                                                                        3. Click **+ Add Slot** to create a new slot (enter slot number and vehicle type)
                                                                                                                                                        4. Click **×** on a tile to delete a slot (blocked if OCCUPIED with active booking)

                                                                                                                                                        ---

                                                                                                                                                        ### Troubleshooting

                                                                                                                                                        | Issue | Cause | Fix |
                                                                                                                                                        |---|---|---|
                                                                                                                                                        | Login fails with 401 | Wrong credentials or account inactive | Check email/password; contact admin |
                                                                                                                                                        | Slots not loading | `parking-service` not running | Start `parking-service` on port 8091 |
                                                                                                                                                        | "You already have an active booking" | Only one active booking allowed | Complete or cancel existing booking first |
                                                                                                                                                        | "Vehicle type mismatch" | Selected slot type doesn't match vehicle | Choose a slot matching your vehicle type |
                                                                                                                                                        | Profile update fails | Session expired | Logout and login again |
                                                                                                                                                        | Blank dates in booking history | Old data with null endTime | Only pre-fix cancelled bookings affected; new ones work correctly |

                                                                                                                                                        ---

                                                                                                                                                        ## 10. Demo Instructions

                                                                                                                                                        ### Demo Video Link
                                                                                                                                                        > *(Insert your demo video link here — YouTube / Google Drive / etc.)*

                                                                                                                                                        ---

                                                                                                                                                        ### Sample Data to Create Before Demo

                                                                                                                                                        **Step 1 — Register Admin User**
                                                                                                                                                        > *(Manually set `role = 'ADMIN'` in DB for the first admin user, or use the role update API)*

                                                                                                                                                        **Step 2 — Create Buildings (as Admin)**
                                                                                                                                                        - Building 1: `Name: Tech Park`, `City: Chennai`, `Address: OMR Road`
                                                                                                                                                        - Building 2: `Name: City Square`, `City: Bangalore`, `Address: MG Road`

                                                                                                                                                        **Step 3 — Create Slots (as Admin → Slots page)**
                                                                                                                                                        - Tech Park: 3 × CAR slots (A1, A2, A3), 2 × BIKE slots (B1, B2)
                                                                                                                                                        - City Square: 2 × CAR slots (C1, C2), 2 × BIKE slots (D1, D2)

                                                                                                                                                        **Step 4 — Register a Normal User**
                                                                                                                                                        - Name: `John Doe`, Email: `john@test.com`, Password: `password123`

                                                                                                                                                        **Step 5 — Register Vehicles for John**
                                                                                                                                                        - Vehicle 1: `TN01AB1234` — CAR — `Honda City`
                                                                                                                                                        - Vehicle 2: `TN01CD5678` — BIKE — `Royal Enfield`

                                                                                                                                                        ---

                                                                                                                                                        ### Execution Steps for Live Demo

                                                                                                                                                        | # | Action | Expected Result |
                                                                                                                                                        |---|---|---|
                                                                                                                                                        | 1 | Login as normal user | Dashboard shows; user nav visible |
                                                                                                                                                        | 2 | Go to Vehicles | List shows registered vehicles |
                                                                                                                                                        | 3 | Go to Bookings → New Booking | 4-step wizard opens |
                                                                                                                                                        | 4 | Select Building → Vehicle → Slot → Confirm | Booking created (ACTIVE) |
                                                                                                                                                        | 5 | Go to Active tab | Booking visible with start time |
                                                                                                                                                        | 6 | Click Exit | Booking moves to Completed with end time + duration |
                                                                                                                                                        | 7 | Login as Admin | Admin dashboard with stats |
                                                                                                                                                        | 8 | Admin → Slots | Live tiles for all buildings |
                                                                                                                                                        | 9 | Admin → Bookings → COMPLETED tab | Just-completed booking visible |
                                                                                                                                                        | 10 | Admin → Users → Expand user row | Vehicles of user shown |

                                                                                                                                                        ---

                                                                                                                                                        ## 11. Innovation & Impact

                                                                                                                                                        ### Business Impact
                                                                                                                                                        - **Eliminates manual parking logs** — fully digital booking and release cycle.
                                                                                                                                                        - **Reduces slot conflicts** — active booking guard prevents double-booking at DB level.
                                                                                                                                                        - **Real-time occupancy** — operators see live slot status without polling physical sensors.
                                                                                                                                                        - **Multi-building support** — one platform manages any number of buildings and their slots.
                                                                                                                                                        - **Audit trail** — every booking has a full lifecycle record (created, completed/cancelled, timestamps, user, vehicle, slot).

                                                                                                                                                        ### User Impact
                                                                                                                                                        - **Zero search time** — users see only available slots, filtered by their vehicle type.
                                                                                                                                                        - **Advance awareness** — users can see which buildings have availability before travelling.
                                                                                                                                                        - **Full booking history** — users can track all past parking sessions with duration.
                                                                                                                                                        - **Self-service** — users manage vehicles and profile without admin intervention.

                                                                                                                                                        ### Technical Innovation
                                                                                                                                                        - **Cross-service validation at booking time** — `parking-service` calls `myapp` via Spring WebClient to verify vehicle ownership before creating a booking, ensuring data integrity across microservice boundaries.
                                                                                                                                                        - **Vehicle-type-aware slot filtering** — slots are never shown if they don't match the selected vehicle type, preventing backend errors before they happen.
                                                                                                                                                        - **Zone-safe Angular rendering** — all HTTP callbacks use `ChangeDetectorRef.detectChanges()` explicitly, ensuring Angular 19's zoneless-compatible rendering works reliably.
                                                                                                                                                        - **Java LocalDateTime nanosecond fix** — a `safeDate()` helper in the frontend truncates Java's 6-digit nanosecond timestamps to 3-digit milliseconds before passing to Angular's date pipe, making all date displays reliable.
                                                                                                                                                        - **JWT-only session** — no server-side session storage; all user context derived from the JWT payload, making the backend fully stateless and horizontally scalable.

                                                                                                                                                        ### Scalability Benefits
                                                                                                                                                        - **Microservice architecture** — `myapp` and `parking-service` can be scaled independently based on load (e.g., scale parking-service during peak hours).
                                                                                                                                                        - **Stateless JWT auth** — no session affinity required; any backend instance can serve any request.
                                                                                                                                                        - **Lazy-loaded Angular modules** — frontend only loads code for the active route, keeping initial bundle size small.
                                                                                                                                                        - **Database per service** — each microservice owns its own schema, allowing independent migrations and zero cross-schema joins.
                                                                                                                                                        - **REST + WebClient** — inter-service calls are non-blocking reactive HTTP, keeping the booking creation path efficient even under load.

                                                                                                                                                        ---

## 13. Limitations

### Known Issues

| # | Issue | Impact | Workaround |
|---|---|---|---|
| 1 | Old cancelled bookings (created before the `endTime` fix) show `—` for cancellation time | Display only — no functional impact | Data issue in DB; new cancellations work correctly |
| 2 | JWT does not auto-refresh — token expires after configured TTL | User is silently logged out on next API call | User must log in again; no token refresh endpoint yet |
| 3 | No real-time push updates — slot occupancy tiles require manual page refresh to reflect changes made by other users | Stale occupancy display in multi-user scenarios | Refresh the Slots page manually |
| 4 | Inter-service call in `createBooking` uses `WebClient.block()` (blocking call inside a reactive chain) | Minor performance concern under very high concurrency | Acceptable for current hackathon scale; will be replaced with async in production |
| 5 | No pagination on Admin Bookings / Users lists | Large datasets may slow down the UI | Manageable at current data scale |
| 6 | Slot numbers are free-text — no duplicate check enforced at DB level | Admin could accidentally create duplicate slot numbers in the same building | Admin must ensure uniqueness manually; DB constraint to be added |
| 7 | Vehicle number uniqueness is per-system, not per-building or per-slot | Edge case only | No user-facing impact |

---

### Mock / Seed Data Dependencies
- The first **Admin user** must be manually promoted via direct DB update or the `PUT /uvmgmt/users/{userId}/role` API since registration always creates `USER` role.
- Buildings and slots must be created by Admin before any user can book — the system has no pre-seeded data.
- There is no bulk import for slots; each slot must be created individually via the Admin UI.

---

### Time Constraints
- **Promote-to-Admin UI** in the Admin Users panel was not implemented in the frontend (backend API exists: `PUT /uvmgmt/users/{userId}/role`).
- **Building detail page** (`/buildings/:id`) route exists but the detail component was not built; clicking a building navigates to the list only.
- **Email notifications** (booking confirmation, cancellation) were planned but not implemented due to time.
- **Payment integration** was out of scope for this submission.
- **Mobile responsiveness** is functional via Bootstrap grid but was not fine-tuned for all screen sizes.

---

## 14. Future Enhancements

### Additional Features

| Feature | Description | Priority |
|---|---|---|
| **Real-time slot updates** | WebSocket / SSE push to update slot tiles without page refresh | High |
| **QR Code for booking** | Generate a QR code on booking confirmation for barrier-gate scanning | High |
| **Advance / scheduled booking** | Book a slot for a future date/time with configurable duration | High |
| **Pricing & Payment** | Per-hour pricing model with Razorpay / Stripe integration | High |
| **Email & SMS notifications** | Send booking confirmation, reminder, and cancellation alerts | Medium |
| **Promote user to Admin** | Admin UI button to change a user's role to ADMIN | Medium |
| **Building detail page** | Dedicated page showing building info + all its slots and live occupancy | Medium |
| **Search & filter buildings** | Filter buildings by city, availability, vehicle type | Medium |
| **Booking extension** | Allow user to extend an active booking before auto-expiry | Medium |
| **Auto-complete expired bookings** | Cron job to auto-complete bookings that exceed a max duration | Medium |
| **Review & rating** | Users can rate parking lots after completion | Low |
| **Parking history export** | Download booking history as PDF or CSV | Low |

---

### Integrations

| Integration | Purpose |
|---|---|
| **Google Maps API** | Show building location on map; directions from user location |
| **Razorpay / Stripe** | Collect parking fees at booking or exit |
| **Twilio / Firebase** | SMS / push notification for booking lifecycle events |
| **Spring Cloud Gateway** | API Gateway to route both microservices through a single entry point |
| **Eureka Service Registry** | Dynamic service discovery instead of hardcoded `localhost` URLs |
| **Redis Cache** | Cache building/slot lists to reduce DB load on read-heavy endpoints |
| **AWS S3 / Cloudinary** | Store vehicle images or parking receipts |

---

### Production Readiness Steps

| Step | Description |
|---|---|
| **1. HTTPS / TLS** | Deploy behind NGINX with SSL certificates (Let's Encrypt) |
| **2. JWT Refresh Token** | Implement refresh token endpoint to silently renew expired sessions |
| **3. API Gateway** | Route all frontend traffic through Spring Cloud Gateway; hide internal service ports |
| **4. Service Discovery** | Replace hardcoded `localhost:8090` inter-service URLs with Eureka + service names |
| **5. Containerization** | Dockerize both services + Angular app; create `docker-compose.yml` |
| **6. CI/CD Pipeline** | GitHub Actions / Jenkins pipeline for automated build, test, deploy |
| **7. Database Migrations** | Replace `ddl-auto=update` with Flyway / Liquibase for controlled schema migrations |
| **8. Centralized Logging** | ELK Stack (Elasticsearch + Logstash + Kibana) or Grafana Loki |
| **9. Health Monitoring** | Spring Boot Actuator + Prometheus + Grafana dashboards |
| **10. Rate Limiting** | API Gateway rate limiting to prevent abuse |
| **11. Input Validation** | Add `@Valid` + Bean Validation annotations on all request bodies |
| **12. Environment Config** | Externalize all secrets/URLs to environment variables or Spring Cloud Config |

---

## 15. Conclusion

### Summary of Value

The **Smart Parking System** successfully delivers a complete, end-to-end digital parking management solution built on modern microservice architecture. Within a single hackathon sprint, the team:

- Built **two independent Spring Boot microservices** with a shared JWT security layer and cross-service validation.
- Delivered a **full Angular 19 frontend** with separate User and Admin experiences, covering 15+ screens and 30+ API integrations.
- Implemented a **complete booking lifecycle** — from vehicle registration through slot selection, booking, completion, and cancellation — with all edge cases handled at both the API and UI layer.
- Solved **real technical challenges** including Java nanosecond date parsing, Angular zone context loss, microservice cross-validation, and live slot occupancy management.

---

### Final Pitch Message

> Parking is a daily problem for millions of urban commuters. The Smart Parking System transforms this broken, manual experience into a seamless digital workflow — from the moment a driver registers their vehicle to the moment they exit a slot. Our platform gives **users time back** and gives **operators full control**, all through a clean, role-aware interface backed by a resilient microservice architecture.
>
> SPS is not just a demo — it is a **production-ready foundation**. Add a payment gateway, plug in a QR scanner at the gate, deploy on AWS with Docker — and this becomes a real product that any parking operator could run tomorrow.
>
> **Smart parking. Smarter cities.**

---

## 16. Appendix

### A. API Reference

#### `myapp` — User & Vehicle Service (Port 8090)

**Auth Endpoints**

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/uvmgmt/users/register` | None | Register new user |
| `POST` | `/uvmgmt/users/login` | None | Login; returns JWT token |
| `GET` | `/uvmgmt/users/{userId}` | JWT (Self/Admin) | Get user by ID |
| `PUT` | `/uvmgmt/users/{userId}` | JWT (Self only) | Update user profile |
| `GET` | `/uvmgmt/users` | JWT (Admin) | Get all users |
| `PUT` | `/uvmgmt/users/{userId}/role` | JWT (Admin) | Update user role |

**Vehicle Endpoints**

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/uvmgmt/vehicles` | JWT (User) | Register a vehicle |
| `GET` | `/uvmgmt/vehicles/my` | JWT (User) | Get my vehicles |
| `GET` | `/uvmgmt/vehicles/{id}` | JWT (User/Admin) | Get vehicle by ID |
| `PUT` | `/uvmgmt/vehicles/{id}` | JWT (User) | Update vehicle |
| `DELETE` | `/uvmgmt/vehicles/{id}` | JWT (User/Admin) | Remove vehicle |
| `GET` | `/uvmgmt/vehicles/user/{userId}` | JWT (Admin) | Get vehicles by user ID |

**Sample Response Wrapper (all endpoints)**
```json
{
  "message": {
    "responseCode": "200",
    "responseMessage": "Success"
  },
  "appResponse": { ... }
}
```

---

#### `parking-service` — Parking Service (Port 8091)

**Building Endpoints**

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/pabsm/buildings` | JWT | Get all buildings |
| `GET` | `/pabsm/buildings/{id}` | JWT | Get building by ID |
| `POST` | `/pabsm/buildings` | JWT (Admin) | Create building |

**Slot Endpoints**

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/pabsm/slots/building/{buildingId}` | JWT | Get all slots in a building |
| `GET` | `/pabsm/slots/building/{buildingId}/available` | JWT | Get available slots (with `?vehicleType=CAR/BIKE`) |
| `POST` | `/pabsm/slots` | JWT (Admin) | Create a slot |
| `DELETE` | `/pabsm/slots/{slotId}` | JWT (Admin) | Delete a slot (blocked if ACTIVE booking exists) |

**Booking Endpoints**

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/pabsm/bookings` | JWT (User) | Create booking `{ vehicleId, slotId }` |
| `GET` | `/pabsm/bookings/my` | JWT (User) | Get my bookings |
| `PUT` | `/pabsm/bookings/{id}/complete` | JWT (User) | Complete (exit) a booking |
| `PUT` | `/pabsm/bookings/{id}/cancel` | JWT (User) | Cancel a booking |
| `GET` | `/pabsm/bookings` | JWT (Admin) | Get all bookings |
| `GET` | `/pabsm/bookings/status/{status}` | JWT (Admin) | Get bookings by status (ACTIVE/COMPLETED/CANCELLED) |
| `GET` | `/pabsm/bookings/user/{userId}` | JWT (Admin/Self) | Get bookings by user ID |

---

### B. Database Schema (SQL Scripts)

#### `myapp_db`

```sql
CREATE TABLE users (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(100)        NOT NULL,
  email        VARCHAR(150) UNIQUE NOT NULL,
  password     VARCHAR(255)        NOT NULL,
  role         VARCHAR(10)         DEFAULT 'USER',
  status       VARCHAR(20)         DEFAULT 'ACTIVE',
  created_on   DATETIME,
  created_by   BIGINT,
  updated_on   DATETIME,
  updated_by   BIGINT
);

CREATE TABLE vehicles (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  vehicle_number VARCHAR(20) UNIQUE NOT NULL,
  vehicle_type   VARCHAR(10)        NOT NULL,  -- CAR | BIKE
  model          VARCHAR(100),
  created_on     DATETIME,
  created_by     BIGINT,
  updated_on     DATETIME,
  updated_by     BIGINT
);

CREATE TABLE user_vehicles (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id        BIGINT NOT NULL,
  vehicle_id     BIGINT NOT NULL,
  ownership_type VARCHAR(20),                  -- OWNER | FAMILY
  created_on     DATETIME,
  created_by     BIGINT,
  UNIQUE (user_id, vehicle_id)
);
```

#### `parking_db`

```sql
CREATE TABLE buildings (
  building_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(150) NOT NULL,
  city         VARCHAR(100) NOT NULL,
  address      VARCHAR(255),
  created_on   DATETIME,
  created_by   BIGINT,
  updated_on   DATETIME,
  updated_by   BIGINT
);

CREATE TABLE slots (
  slot_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  slot_number  VARCHAR(20)  NOT NULL,
  vehicle_type VARCHAR(10)  NOT NULL,   -- CAR | BIKE
  building_id  BIGINT       NOT NULL,
  status       VARCHAR(20)  DEFAULT 'AVAILABLE',  -- AVAILABLE | OCCUPIED
  created_on   DATETIME,
  created_by   BIGINT,
  updated_on   DATETIME,
  updated_by   BIGINT
);

CREATE TABLE bookings (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      BIGINT       NOT NULL,
  vehicle_id   BIGINT       NOT NULL,
  slot_id      BIGINT       NOT NULL,
  building_id  BIGINT       NOT NULL,
  status       VARCHAR(20)  NOT NULL,   -- ACTIVE | COMPLETED | CANCELLED
  start_time   DATETIME,
  end_time     DATETIME,
  created_on   DATETIME,
  created_by   BIGINT,
  updated_on   DATETIME,
  updated_by   BIGINT
);
```

---

### C. Entity Relationship Diagram (ERD)

```
myapp_db
─────────────────────────────────────────────────────────
 users                user_vehicles          vehicles
┌──────────┐         ┌──────────────┐       ┌──────────────┐
│ id  (PK) │◄────────│ user_id (FK) │       │ id  (PK)     │
│ name     │         │ vehicle_id(FK)│──────►│ vehicle_number│
│ email    │         │ ownership_type│       │ vehicle_type  │
│ password │         └──────────────┘       │ model         │
│ role     │                                └──────────────┘
│ status   │
└──────────┘

parking_db
─────────────────────────────────────────────────────────
 buildings             slots                bookings
┌─────────────┐       ┌────────────┐       ┌────────────────┐
│ building_id │◄──────│ building_id│       │ id (PK)        │
│ (PK)        │       │ (FK)       │◄──────│ slot_id (FK)   │
│ name        │       │ slot_id(PK)│       │ building_id(FK)│
│ city        │       │ slot_number│       │ user_id        │
│ address     │       │ vehicle_type       │ vehicle_id     │
└─────────────┘       │ status     │       │ status         │
                      └────────────┘       │ start_time     │
                                           │ end_time       │
                                           └────────────────┘
```

---

### D. JWT Payload Structure

```json
{
  "sub":    "john@example.com",
  "userId": 5,
  "name":   "John Doe",
  "role":   "USER",
  "iat":    1713400000,
  "exp":    1713486400
}
```

- `sub` — user email (Spring Security principal)
- `userId` — extracted by frontend for all API calls requiring `{userId}`
- `role` — `USER` or `ADMIN` — drives route guards and navbar separation
- `iat` / `exp` — issued-at / expiry (Unix timestamps)

---

### E. Angular Route Map

```
/                          → redirect to /auth/login
/auth/login                → LoginComponent
/auth/register             → RegisterComponent
/dashboard                 → DashboardComponent           [AuthGuard]
/profile                   → ProfileComponent             [AuthGuard]
/vehicles                  → VehicleListComponent         [AuthGuard]
/vehicles/add              → VehicleFormComponent         [AuthGuard]
/bookings                  → BookingListComponent         [AuthGuard]
/bookings/new              → BookingFormComponent         [AuthGuard]
/buildings                 → BuildingListComponent        [AuthGuard]
/admin                     → AdminDashboardComponent      [AuthGuard]
/admin/bookings            → AdminBookingsComponent       [AuthGuard]
/admin/users               → AdminUsersComponent          [AuthGuard]
/admin/slots               → AdminSlotsComponent          [AuthGuard]
/admin/buildings/create    → AdminCreateBuildingComponent [AuthGuard]
```

---

*End of Documentation — Smart Parking System (SPS)*
