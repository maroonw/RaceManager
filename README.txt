# Race Manager

A Java-based race management system demonstrating clean software architecture with MVC, Strategy, and Observer design patterns.

## Overview

Race Manager handles cycling race operations including user registration, race signups, payment processing, and real-time notifications. The system showcases how design patterns solve real-world problems in a racing application context.

## Features

- User authentication and role-based access (Racer, Administrator, Organizer)
- Race creation and browsing with eligibility checking
- Multiple payment methods (Credit Card, PayPal, Stripe)
- Automatic license issuance for official races
- Real-time event notifications
- Results tracking and race reviews

## Architecture: MVC Pattern

**Model**: Business entities 
**View**: UI components 
**Controller**: Request handlers 

**Why MVC?** Separates business logic from presentation, making the codebase testable and maintainable. You can change the UI without touching race management logic.

##  Design Patterns

### Strategy Pattern: Payment Processing
**Problem**: The system needs to support multiple payment methods, each with different processing logic.

**Implementation**:
model/payment/ 
├── PaymentStrategy.java # Interface defining pay() method 
├── PaymentService.java # Context that uses strategies 
├── CreditCardPayment.java # Concrete strategy 
├── PayPalPayment.java # Concrete strategy 
└── StripePayment.java # Concrete strategy

### Observer Pattern: Event Notifications
Problem: Racers need to be notified about race events (signups, payments, results) without tight coupling between components.
Implementation:
model/notification/
├── IObserver.java              # Observer interface with update() method
├── ISubject.java               # Subject interface (attach/detach/notify)
├── RaceNotifications.java      # Concrete subject for race events
├── NotificationCenter.java     # Global notification hub
└── RacerSubscriber.java        # Concrete observer (racers)

## Getting Started
### Prerequisites
Java JDK 17 or higher
IntelliJ IDEA (recommended)
### Run the Application
Open the project in IntelliJ IDEA
Navigate to RaceManager/src/app/Main.java
Right-click and select "Run 'Main.main()'"

