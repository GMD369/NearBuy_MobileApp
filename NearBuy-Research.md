# NearBuy — Research Findings

## 1. Competitor Analysis

| App | Key UI Pattern | Takeaway for NearBuy |
|-----|---------------|----------------------|
| OLX Pakistan | Grid/list toggle, category chips, FAB for posting | Dominant in PK — must match baseline UX |
| Facebook Marketplace | Location radius slider, "Is this available?" quick msg | Hyper-local + instant messaging drives engagement |
| OfferUp | TrustScore, one-tap "Make Offer" | Trust & frictionless offers = more conversions |
| Carousell | Instagram-like grid, seller badges, bump feature | Clean minimal design works best |
| Mercari | Smart pricing, barcode scan listing | AI-assisted listing creation is a differentiator |

**Common patterns across all:** Bottom nav (5 tabs), card-based layouts, image-first design, pull-to-refresh, skeleton loading, location on every card.

---

## 2. Pakistan Market Context

- **Dominant players:** OLX PK, Facebook buy/sell groups, Daraz (formal e-commerce)
- **Payments:** JazzCash, Easypaisa, SadaPay/NayaPay (younger users), cash-on-meetup still dominant
- **Culture:** Bargaining expected, trust is low (prefer COD/meetup), WhatsApp preferred for chat
- **Language:** Roman Urdu widely used — search must handle it
- **Seasons:** Eid/Ramadan sales are peak
- **Gap:** No swap marketplace, no AI-powered listings, no hyper-local (neighborhood-level) app, university marketplaces underserved

---

## 3. Android Widgets Checklist (For Mid Submission)

### Navigation & Structure
- BottomNavigationView, Navigation Component (NavGraph), DrawerLayout, CollapsingToolbarLayout

### Lists & Cards
- RecyclerView (multiple view types, nested horizontal+vertical), CardView, SwipeRefreshLayout, ViewPager2, StaggeredGridLayoutManager

### Input & Forms
- TextInputLayout (with validation), ChipGroup/Chip, Slider/RangeSlider, SwitchMaterial, RadioGroup, RatingBar

### Feedback & Dialogs
- Snackbar, MaterialAlertDialog, BottomSheetDialogFragment, ProgressIndicator

### Media & Visual
- ShapeableImageView, FloatingActionButton, Shimmer loading, SharedElement transitions

### Data & State
- SharedPreferences, ViewBinding, ViewModel + LiveData, DiffUtil + ListAdapter

---

## 4. Swap Mode UI Flow

1. **Listing creation** — "Open to Swap?" toggle + "Looking for" field
2. **Propose Swap** — select your item from grid → side-by-side comparison card (your item vs their item) → optional cash top-up → confirm
3. **Swap Proposals tab** — received / sent proposals with Accept/Decline/Counter buttons
4. **Fairness indicator** — price comparison bar on each proposal

```kotlin
data class SwapProposal(
    val id: String,
    val offeredItem: Listing,
    val requestedItem: Listing,
    val additionalCash: Double?,
    val status: SwapStatus, // PENDING, ACCEPTED, DECLINED, COMPLETED
    val proposedBy: User,
    val proposedTo: User
)
```

---

## 5. Recommended MVVM Folder Structure

```
com.nearbuy.app/
├── data/
│   ├── model/        (Listing, User, Category, SwapProposal)
│   ├── repository/   (ListingRepo, UserRepo, SwapRepo)
│   └── mock/         (MockListings, MockUsers, MockCategories)
├── ui/
│   ├── onboarding/   (ViewPager2 slides)
│   ├── auth/         (Login, Register)
│   ├── home/         (HomeFragment + nested adapters)
│   ├── browse/       (BrowseFragment + filters)
│   ├── detail/       (DetailActivity)
│   ├── post/         (Multi-step post flow)
│   ├── swap/         (SwapFragment + proposals)
│   ├── chat/         (Mock chat UI)
│   └── profile/      (ProfileFragment)
└── utils/            (Constants, Extensions)
```

---

## 6. Post-Mid Standout Features

| Feature | Why It's Unique |
|---------|----------------|
| AI Swap Matchmaker | Detects circular swap chains (A→B→C→A) — no competitor does this |
| AI Listing Creator | Photo → auto title, description, category, price via Claude API |
| Deal Score | Rates listings as great/fair/overpriced based on local data |
| Neighborhood Feed | 500m–2km radius, not city-wide — creates urgency |
| AI Negotiation Assistant | Suggests counter-offers, flags lowballs |
| Verified Handoff Zones | Suggests safe public meetup spots |
| Price Drop Alerts | Notify watchers when price drops |

---

## 7. SaaS Potential (Post-University)

- **White-label** for university campuses, housing societies, corporate campuses
- **Revenue:** Promoted listings, transaction fees (JazzCash/Easypaisa integration), premium seller tools
- **Moat:** AI features + swap matching + hyper-local focus
