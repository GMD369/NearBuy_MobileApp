# NearBuy — Development Roadmap

## Phase 1: Pre-Mids / Frontend Only (Mar 19 – Apr 2, 2 weeks)

> Goal: Fully functional Android UI with mock data, demonstrating all required widgets.

### Week 1 (Mar 19 – Mar 25): Core Screens & Navigation

- [ ] Project setup (MVVM structure, ViewBinding, Navigation Component, dependencies)
- [ ] **Bottom Navigation** (Home, Browse, Post, Swap, Profile) + NavGraph
- [ ] **Onboarding** — ViewPager2 slides (3-4 screens) with skip/next
- [ ] **Auth screens** — Login & Register (TextInputLayout with validation, SwitchMaterial for "Remember me")
- [ ] **Home screen** — CollapsingToolbarLayout, horizontal category chips (ChipGroup), vertical RecyclerView of listing cards (CardView + ShapeableImageView), pull-to-refresh (SwipeRefreshLayout), shimmer/skeleton loading
- [ ] **Mock data layer** — MockListings, MockUsers, MockCategories populated with realistic Pakistani marketplace data
- [ ] ViewModel + LiveData wired to mock repos

### Week 2 (Mar 26 – Apr 1): Remaining Screens & Polish

- [ ] **Browse/Search** — Filter bottom sheet (BottomSheetDialogFragment), RangeSlider for price, RadioGroup for condition, ChipGroup for categories, StaggeredGridLayoutManager toggle
- [ ] **Post Listing** — Multi-step flow (ViewPager2 or NavGraph), image picker placeholder, "Open to Swap?" SwitchMaterial, category Chips, Slider for price, RatingBar for condition
- [ ] **Listing Detail** — SharedElement transition from card, image carousel (ViewPager2), FAB for "Make Offer" / "Message Seller", Snackbar confirmations, MaterialAlertDialog for offer
- [ ] **Swap screens** — Propose swap (side-by-side card comparison), swap proposals tab (RecyclerView with sent/received, Accept/Decline buttons), fairness indicator (ProgressIndicator bar)
- [ ] **Chat screen** — Mock chat UI (RecyclerView with sent/received message view types)
- [ ] **Profile screen** — DrawerLayout with settings, user avatar (ShapeableImageView), listings grid, RatingBar
- [ ] Final polish: consistent theming (Material 3), DiffUtil + ListAdapter on all lists, SharedPreferences for dark mode / onboarding-seen flag
- [ ] **Widget audit** — cross-check every widget from the checklist is used at least once

### Mid Submission Deliverable
- APK with full navigation, all screens populated with mock data, every required Android widget demonstrated.

---

## Phase 2: Post-Mids / Backend + AI Features (Apr 3 – May 28, ~8 weeks)

### Week 3–4 (Apr 3 – Apr 16): Backend Foundation

- [ ] Firebase/Supabase setup (Auth, Firestore/Postgres, Storage)
- [ ] Replace mock repos with real data layer (Retrofit / Firebase SDK)
- [ ] User auth (email + Google sign-in)
- [ ] CRUD for listings (create, read, update, delete) with image upload
- [ ] Location services — GPS-based neighborhood feed (500m–2km radius)
- [ ] Real-time chat (Firebase Realtime DB or WebSockets)

### Week 5–6 (Apr 17 – Apr 30): Swap Engine & Payments

- [ ] Swap proposal backend (create, accept, decline, counter)
- [ ] Swap status tracking + push notifications (FCM)
- [ ] JazzCash/Easypaisa integration for cash top-up on swaps
- [ ] Search backend — full-text search handling Roman Urdu
- [ ] Seller ratings & trust score system

### Week 7–8 (May 1 – May 14): AI Features

- [ ] **AI Listing Creator** — photo → auto title, description, category, price suggestion (Claude API)
- [ ] **Deal Score** — rates listings as great / fair / overpriced based on local comparable data
- [ ] **AI Swap Matchmaker** — detect circular swap chains (A→B→C→A)
- [ ] **AI Negotiation Assistant** — suggest counter-offers, flag lowball offers
- [ ] Price drop alerts (notify watchers)

### Week 9–10 (May 15 – May 28): Polish & Launch Prep

- [ ] Verified handoff zones (map integration with safe meetup spots)
- [ ] Performance optimization (pagination, image caching with Coil/Glide, ProGuard)
- [ ] Edge cases, error handling, offline fallback
- [ ] UI/UX polish pass — animations, transitions, empty states
- [ ] Testing (unit tests for ViewModels, UI tests for critical flows)
- [ ] Final demo prep / documentation

---

## Key Milestones

| Date | Milestone |
|------|-----------|
| Apr 1 | Mid submission — full frontend with mock data |
| Apr 16 | Backend live — real auth, listings, chat |
| Apr 30 | Swap engine + payments working |
| May 14 | AI features integrated |
| May 28 | Final submission ready |

---

## Tech Stack Summary

| Layer | Tech |
|-------|------|
| Language | Kotlin |
| UI | Material 3, XML layouts, ViewBinding |
| Architecture | MVVM (ViewModel + LiveData + Repository) |
| Navigation | Jetpack Navigation Component |
| Backend | Firebase / Supabase |
| AI | Claude API |
| Payments | JazzCash / Easypaisa SDK |
| Images | Coil or Glide |
| Maps | Google Maps SDK |
