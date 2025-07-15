# ShopMate MVVM Architecture Guidelines

## Project Structure

The ShopMate project follows the MVVM (Model-View-ViewModel) architecture pattern with the following directory structure:

```
com.example.shopmate/
├── ui/
│   ├── activities/     # UI containers (Activities)
│   ├── fragments/      # UI components (Fragments)
│   ├── adapters/       # RecyclerView and ViewPager adapters
│   └── widgets/        # Custom views and UI components
├── viewmodel/          # ViewModels
├── data/
│   ├── model/          # Data models/entities
│   ├── repository/     # Repositories (data sources)
│   └── network/        # API services and network-related code
└── util/               # Utility classes and helper functions
```

## Component Responsibilities

### View Layer (UI)

- **Activities**: Container for fragments, handling system interactions
- **Fragments**: UI components displaying data and capturing user input
- **Adapters**: Data binding for lists and collections
- **Widgets**: Custom UI components

### ViewModel Layer

- Bridge between View and Model layers
- Hold and prepare data for the UI
- Handle UI-related business logic
- Survive configuration changes
- Communicate with repositories

### Model Layer (Data)

- **Models**: Data structures/entities
- **Repositories**: Single source of truth for data, abstracting data sources
- **Network**: API interfaces and network operations

## Communication Flow

1. **View → ViewModel**:

   - Views observe LiveData/StateFlow from ViewModels
   - Views call ViewModel methods for user actions

2. **ViewModel → Model**:

   - ViewModels request data from repositories
   - ViewModels process data from repositories

3. **Model → ViewModel**:

   - Repositories return data (via LiveData/Flow) to ViewModels
   - Repositories notify ViewModels of data changes

4. **ViewModel → View**:
   - ViewModels expose LiveData/StateFlow that Views observe
   - Views update UI based on ViewModel state

## Naming Conventions

### Activities

- Name: `[Feature]Activity.java`
- Example: `LoginActivity.java`, `MainActivity.java`

### Fragments

- Name: `[Feature]Fragment.java`
- Example: `HomeFragment.java`, `ProductDetailFragment.java`

### Adapters

- Name: `[Feature]Adapter.java`
- Example: `ProductAdapter.java`, `CategoryAdapter.java`

### ViewModels

- Name: `[Feature]ViewModel.java`
- Example: `HomeViewModel.java`, `ProductDetailViewModel.java`

### Models

- Name: `[Entity].java`
- Example: `Product.java`, `Category.java`

### Repositories

- Name: `[Feature]Repository.java`
- Example: `ProductRepository.java`, `AuthRepository.java`

### API Interfaces

- Name: `[Feature]Api.java`
- Example: `ProductApi.java`, `AuthApi.java`

## Best Practices

### View Layer

- Views should be as dumb as possible, focusing only on displaying data and capturing user input
- No business logic in Views
- Use data binding when appropriate
- Observe LiveData/StateFlow from ViewModels

### ViewModel Layer

- ViewModels should not hold references to Views
- Use LiveData/StateFlow for observable data
- Handle UI-related business logic
- Do not access Android framework classes directly (use AndroidViewModel if needed)

### Model Layer

- Repositories should be the single source of truth for data
- Abstract data sources (API, database, preferences)
- Use appropriate threading for data operations
- Provide clean API to ViewModels

### General

- Follow single responsibility principle
- Use dependency injection where appropriate
- Write unit tests for ViewModels and Repositories
- Keep components focused and cohesive

## File Organization Examples

### Feature: Authentication

- `ui/activities/LoginActivity.java`
- `viewmodel/AuthViewModel.java`
- `data/model/LoginRequest.java`
- `data/model/LoginResponse.java`
- `data/repository/AuthRepository.java`
- `data/network/AuthApi.java`

### Feature: Product Details

- `ui/fragments/ProductDetailFragment.java`
- `viewmodel/ProductDetailViewModel.java`
- `data/model/Product.java`
- `data/repository/ProductRepository.java`
- `data/network/ProductApi.java`

## Data Flow Example

For a login feature:

1. User enters credentials in `LoginActivity`
2. `LoginActivity` calls `authViewModel.login(email, password)`
3. `AuthViewModel` validates input and calls `authRepository.login(loginRequest)`
4. `AuthRepository` makes API call via `AuthApi`
5. `AuthRepository` processes response and emits result via LiveData/Flow
6. `AuthViewModel` receives result and updates its state
7. `LoginActivity` observes state changes and updates UI accordingly
