# Ecommerce OMS — Claude Working Rules

## 1. Project Goal

Build a React + Vite frontend for the existing E-commerce OMS backend.

The backend is the source of truth.

The frontend must:
- use only APIs that actually exist;
- make implemented backend features functional;
- show useful future features as disabled;
- show `Not Implemented` on hover or attempted interaction for disabled features;
- never fake backend functionality;
- preserve the existing backend unless a backend change is explicitly approved.

---

## 2. Database Architecture — IMPORTANT

The application uses TWO databases with clearly separated responsibilities.

### MySQL — Primary Application Database

MySQL is the primary relational database.

It is used for:
- Users
- Products
- Inventory
- other relational/domain data implemented by the backend

The backend uses JPA/Hibernate for MySQL.

Liquibase is used for MySQL database migrations.

### MongoDB — Reviews ONLY

MongoDB is used ONLY for product reviews.

The `ProductReview` entity is a MongoDB document (`product_reviews` collection).

Do NOT assume MongoDB is used for:
- Users
- Products
- Inventory
- Authentication
- other core OMS relational data

### Frontend database rule

The React frontend must NEVER connect directly to MySQL or MongoDB.

The frontend communicates only with Spring Boot REST APIs.

Spring Boot decides whether data comes from MySQL or MongoDB.

---

## 3. Backend Technology

Current backend stack includes:
- Java 21
- Spring Boot
- Spring Security
- Keycloak
- JWT / OAuth2 Resource Server
- MySQL
- JPA/Hibernate
- MongoDB
- Redis caching
- Liquibase
- Swagger/OpenAPI
- Maven

---

## 4. Known Implemented APIs

Always verify these against the actual source code before using them.

### Authentication

POST `/api/v1/auth/login`

Request:
```json
{
  "email": "string",
  "password": "string"
}
```

The backend delegates authentication to Keycloak and returns login/token information.

POST `/api/v1/users/register`

Request:
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

---

### User

GET `/api/v1/users/{id}`

Authentication required.

The backend controls whether the authenticated user can access the requested profile.

---

### Product List

GET `/api/v1/products`

Query parameters:
- `page` default `0`
- `size` default `10`
- `sortBy` default `id`
- `direction` default `asc`
- `keyword` optional
- `minPrice` optional
- `maxPrice` optional

The list returns a Spring `Page<ProductResponse>` inside `APIResponse`.

Current list DTO:

```json
{
  "id": 0,
  "name": "string",
  "price": 0,
  "availableQuantity": 0
}
```

### IMPORTANT — Product Description

The product LIST response intentionally uses `ProductResponse` and does NOT contain `description`.

This is acceptable because list pages should avoid unnecessary payload.

---

### Product Details

GET `/api/v1/products/{id}`

IMPORTANT: This endpoint currently returns `ProductDetailsResponse`, not `ProductResponse`.

`ProductDetailsResponse` contains:

```json
{
  "id": 0,
  "name": "string",
  "price": 0,
  "description": "string",
  "availableQuantity": 0
}
```

Use this endpoint for the frontend product-details page.

Do NOT assume the product list response contains description.

---

### ADMIN Product APIs

POST `/api/v1/admin/products`

ADMIN only.

Request body is `APIRequest<ProductRequest>`:

```json
{
  "data": {
    "name": "string",
    "description": "string",
    "price": 0,
    "availableQuantity": 0
  }
}
```

PUT `/api/v1/admin/products/{id}`

ADMIN only.

Request:

```json
{
  "name": "string",
  "description": "string",
  "price": 0,
  "availableQuantity": 0
}
```

DELETE `/api/v1/admin/products/{id}`

ADMIN only.

This is a soft delete.

PATCH `/api/v1/admin/products/{id}`

ADMIN only.

Restores a deleted product.

GET `/api/v1/admin/products/deleted`

ADMIN only.

Returns deleted products.

---

### Product Reviews

POST `/api/v1/products/{productId}/reviews`

CUSTOMER role required.

Request:

```json
{
  "rating": 1,
  "comment": "string",
  "metadata": {}
}
```

Review data is stored in MongoDB.

Currently there is no verified backend endpoint for:
- listing reviews;
- getting a review by ID;
- updating a review;
- deleting a review.

Do NOT create fake review lists/history.

---

## 5. Product DTO Design

Current backend deliberately separates list and detail responses.

### ProductResponse — list/compact response

```text
id
name
price
availableQuantity
```

### ProductDetailsResponse — detail response

```text
id
name
price
description
availableQuantity
```

### ProductRequest — create/update request

```text
name
description
price
availableQuantity
```

Frontend must respect this distinction.

---

## 6. Current Implemented Frontend Features

These should be functional because the backend supports them:

- Login
- Registration
- Authenticated profile
- Public product listing
- Product pagination
- Product sorting
- Product keyword search
- Product price filtering
- Product details
- ADMIN product creation
- ADMIN product update
- ADMIN product soft delete
- ADMIN product restore
- ADMIN deleted-product list
- CUSTOMER review creation

---

## 7. Current Missing Backend Features

Treat these as NOT IMPLEMENTED unless real backend endpoints are added later:

- Order management
- Cart
- Checkout
- Payments
- Review listing/history
- Review update
- Review deletion
- General admin user management
- Dedicated inventory CRUD
- Any other feature without a verified backend endpoint

If such features appear in the frontend:
- keep them disabled;
- make no API call;
- use no fake/mock backend data;
- show `Not Implemented` on hover or interaction.

---

## 8. Authentication and Roles

Known roles:
- `ADMIN`
- `CUSTOMER`

Frontend should provide role-aware navigation and protected routes.

Frontend authorization is only for UX.

Backend authorization remains the real security boundary.

Use the actual JWT/token flow returned by the backend.

Never put:
- database passwords;
- JWT signing secrets;
- Keycloak client/admin secrets;
- SMTP credentials;
- other backend secrets

into frontend source or frontend environment variables unless the value is explicitly safe to expose publicly.

---

## 9. API Response Handling

Most current endpoints use:

```json
{
  "jsonId": "...",
  "success": true,
  "message": "...",
  "data": {}
}
```

The frontend API layer should handle this consistently.

However, do NOT assume every endpoint has the wrapper.

The review creation endpoint currently returns `ReviewResponse` directly.

---

## 10. UI Requirements

Use:
- React
- Vite
- modern dashboard style
- responsive layout
- professional enterprise UI
- reusable components
- sidebar
- header
- cards
- tables
- forms
- pagination
- search/filter controls
- loading states
- empty states
- error states
- confirmation dialogs
- toast/feedback messages
- accessible tooltips
- clear disabled states

Avoid a generic tutorial/demo appearance.

---

# 11. MANDATORY WORKFLOW — PLAN FIRST

This is extremely important.

DO NOT start coding immediately.

### First response

After receiving the project ZIP and this `CLAUDE.md`:

1. Inspect the repository.
2. Understand the backend architecture.
3. Verify the actual APIs.
4. Verify DTOs and response structures.
5. Verify authentication and roles.
6. Identify implemented functionality.
7. Identify missing functionality.
8. Design the frontend architecture.
9. Create a complete phased implementation plan.

### The first response MUST NOT contain implementation code.

It should contain:

#### A. Project understanding
Short summary of what the existing backend does.

#### B. Implemented features
What can be made functional now.

#### C. Missing features
What must remain disabled.

#### D. Proposed frontend architecture
Folders, API layer, routing, auth strategy, state approach, reusable components.

#### E. Full implementation plan
For example:

```text
Phase 1 — Frontend foundation
Phase 2 — Dashboard/layout
Phase 3 — Authentication
Phase 4 — Protected routing and roles
Phase 5 — Product catalogue
Phase 6 — Product details
Phase 7 — ADMIN product management
Phase 8 — User profile
Phase 9 — CUSTOMER reviews
Phase 10 — Disabled future features
Phase 11 — Testing, integration and polish
```

Claude may adjust these phases after inspecting the real project.

#### F. Important decisions / assumptions

Clearly identify anything that needs my decision.

Then STOP.

Wait for my approval.

---

# 12. PLAN REVIEW PROTOCOL

I must be able to modify the plan before coding starts.

If I say something like:

> Change Phase 5 and Phase 6.

or:

> Merge Phase 2 and Phase 3.

or:

> Add a notification system to the plan.

Then:
- update the plan;
- show the revised relevant phases;
- do NOT code;
- wait again.

If I say:

> Done

or:

> Plan approved

then start Phase 1 only.

Do NOT automatically start multiple phases.

---

# 13. PHASE PROTOCOL

Before starting a phase:

1. State the phase goal.
2. Explain what will be implemented.
3. List files likely to be created/changed.
4. Explain important decisions.
5. Explain how the phase will be verified.
6. Ask for confirmation if the phase has not already been approved.

After implementation:

1. Summarize changes.
2. List changed files.
3. Explain how to run/test the phase.
4. Run the smallest relevant verification.
5. Report the ACTUAL result.
6. Mention blockers/issues.
7. STOP.

Never automatically begin the next phase.

Wait for:

> Done, start next phase.

---

# 14. Existing Code Protection

The existing backend is valuable working code.

Do NOT:
- rewrite the backend unnecessarily;
- regenerate existing classes;
- replace working implementations for stylistic reasons;
- refactor unrelated code;
- modify database design just to simplify frontend work.

Before changing an existing file:
1. inspect it;
2. understand why the current implementation exists;
3. identify the minimum required change;
4. preserve existing behavior.

If a frontend issue is caused by an actual backend limitation:
- report it first;
- propose the change;
- wait for approval before making a non-trivial backend change.

---

# 15. No Duplicate Code

Do not output or regenerate entire files when only a small section changes.

Do not paste unchanged source code into the response.

The code belongs in project files.

Chat responses should contain concise summaries and only small code snippets when necessary.

---

# 16. TOKEN EFFICIENCY RULES

Token usage matters.

Always optimize context.

### Before reading files

Search for the relevant:
- class;
- endpoint;
- DTO;
- method;
- configuration;
- dependency.

Then read only the relevant sections.

### Do NOT

- reread the entire repository for every phase;
- repeatedly print the same architecture;
- repeatedly explain unchanged files;
- dump complete files into chat;
- regenerate existing components;
- install unnecessary dependencies;
- create unnecessary abstractions;
- perform unrelated refactoring;
- run expensive checks unrelated to the current phase.

### DO

- maintain a compact working understanding;
- reuse information already verified;
- make targeted edits;
- inspect only files required by the current phase;
- verify only the current phase;
- keep responses concise;
- use existing components where possible.

---

# 17. Testing

At the end of every phase:

Run the smallest relevant verification.

Examples:
- frontend lint;
- frontend build;
- targeted test;
- backend test only if backend was changed.

Do not run unrelated expensive tests unnecessarily.

Never claim a test/build passed unless it was actually run.

Report:

```text
Command:
Result:
```

---

# 18. Decision Rule

If something is unclear:

1. Inspect the repository first.
2. If the answer can be derived from the code, do not ask me.
3. If still unclear and the decision materially affects architecture, behavior, security, database design, or API contracts, ask me.
4. Do not invent backend behavior.

---

# 19. Final Priority

Always prioritize:

1. Correctness
2. Existing backend preservation
3. API compatibility
4. Security
5. Maintainability
6. Clean UX
7. Token efficiency
8. Speed

Do not sacrifice correctness merely to finish faster.
