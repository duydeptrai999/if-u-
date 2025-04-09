# Codebase Structure

## App Structure

- **Package**: com.example.ifu
- **MainActivity** (Home screen of the Easy Recovery app)
  - onCreate: Initializes the app UI
  - setupUI: Sets up click listeners for recovery option cards
  - updateStatistics: Updates statistics display with recovered data info
  - formatSize: Formats byte sizes to human-readable form (B, KB, MB, GB)

## Resources

### Layouts
- **activity_main.xml**: Main screen layout with recovery options and statistics

### Drawables
- **ic_photo.xml**: Vector icon for photo recovery
- **ic_video.xml**: Vector icon for video recovery
- **ic_file.xml**: Vector icon for other files recovery
- **ic_settings.xml**: Vector icon for settings
- **bg_card.xml**: Background drawable for cards
- **progress_bar_colors.xml**: Customized progress bar with gradient colors

### Values
- **strings.xml**: String resources for the app (English)
- **strings.xml (vi)**: Vietnamese translations for the app

## Features

1. Photo Recovery
   - UI components for initiating photo scan and recovery
   - Click listener placeholder for implementation

2. Video Recovery
   - UI components for initiating video scan and recovery
   - Click listener placeholder for implementation

3. Other Files Recovery
   - UI components for initiating file scan and recovery
   - Click listener placeholder for implementation

4. Statistics Display
   - Shows count and size of recovered items
   - Categorizes by photos, videos, and other files
   - Visual progress indicator

## UI Components

- **Cards**: Used for recovery options and statistics display
- **Icons**: Vector drawables for various file types
- **Progress Bar**: Custom progress bar for visual statistics
- **Text Views**: For displaying counts and sizes
