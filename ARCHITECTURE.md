# Architecture Overview

## MVVM Pattern Implementation

The Comixa app implements the Model-View-ViewModel (MVVM) architectural pattern to ensure clean separation of concerns and maintainability.

### Components

#### 1. View (Fragments)

Each sub-section is represented by a Fragment that:
- Handles UI rendering
- Observes LiveData from ViewModels
- Uses View Binding for type-safe view access
- Has no business logic

Example: `HomeSection1Fragment.kt`

#### 2. ViewModel

Each Fragment has a corresponding ViewModel that:
- Manages UI-related data
- Survives configuration changes
- Exposes data via LiveData
- Inherits from `BaseViewModel`

Example: `HomeSection1ViewModel.kt`

#### 3. MainActivity

The main activity:
- Hosts the DrawerLayout
- Manages the NavController
- Sets up the Toolbar
- Coordinates navigation between sections

## Navigation Structure

### Navigation Graph (`nav_graph.xml`)

Defines all 12 destinations (fragments) with:
- Unique IDs matching menu items
- Fragment class references
- Labels from string resources

### Drawer Menu (`drawer_menu.xml`)

Hierarchical menu with:
- 6 top-level items (main sections)
- 2 sub-items each (sub-sections)
- Icons and labels
- Checkable behavior

## Material3 Theming

The app uses Material3 (Material You) design system with:

### Dark Theme Colors

- Primary: Blue tones (#A8C7FA)
- Secondary: Gray-blue tones
- Tertiary: Purple tones
- Surface: Dark backgrounds (#1A1C1E)
- Custom color palette following Material3 guidelines

### Components

- NavigationView for the drawer
- MaterialToolbar for the app bar
- Modern Material3 styling throughout

## Data Flow

```
User Action → Fragment → ViewModel → LiveData → Fragment → UI Update
```

1. User interacts with UI (Fragment)
2. Fragment observes ViewModel's LiveData
3. ViewModel updates LiveData
4. Fragment receives update and refreshes UI

## Adding New Sections

To add a new section:

1. Create Fragment in `ui/[section]/[subsection]/`
2. Create ViewModel in `viewmodel/`
3. Add layout if needed
4. Update `nav_graph.xml` with new destination
5. Update `drawer_menu.xml` with new menu item
6. Add strings to `strings.xml`
7. Update MainActivity's AppBarConfiguration if needed

## Best Practices

- Use View Binding for all views
- Keep Fragments lightweight
- Put business logic in ViewModels
- Use LiveData for observable data
- Follow Material Design guidelines
- Maintain consistent naming conventions
