package com.example.data.model

object CatalogData {

  val SERVICES = listOf(
    // Bedroom Services
    ServiceItem(
      id = "wardrobe_org",
      name = "Wardrobe Organization",
      category = ServiceCategory.BEDROOM,
      tagline = "Curated closets that make getting dressed effortless",
      description = "Full wardrobe transformation including seasonal sorting, categorized hanging systems, space-maximizing velvet hangers, color-coordinated garment grouping, and custom folding techniques.",
      startingPrice = 149.0,
      estimatedHours = 3.5,
      rating = 4.95f,
      reviewsCount = 384,
      popular = true,
      colorSeed = 0xFF5B5BD6,
      included = listOf(
        "Complete clothing purge & categorical sorting",
        "Color-coordinated hanging and vertical folding",
        "Shoe & accessory display arrangement",
        "Free pack of 20 slimline velvet non-slip hangers",
        "Labeling of seasonal bins and top shelf bins"
      ),
      excluded = listOf(
        "Dry cleaning or laundering dirty garments",
        "Structural carpentry or installing permanent shelf fixtures"
      ),
      benefits = listOf(
        "Save 25+ minutes choosing outfits each morning",
        "Double your functional closet hanging capacity",
        "Keep seasonal clothing dust-free and moth-protected"
      ),
      beforeSummary = "Piled garments, mismatched wire hangers, hard-to-find sweaters, cluttered floor piles.",
      afterSummary = "Boutique-style closet with color gradient flow, tidy shelf dividers, labeled acrylic organizers."
    ),
    ServiceItem(
      id = "clothes_sort",
      name = "Clothes Sorting & Declutter",
      category = ServiceCategory.BEDROOM,
      tagline = "Streamline your wardrobe with gentle, expert guidance",
      description = "Mindful decluttering session guiding you through keep, donate, alter, and recycle piles without overwhelm.",
      startingPrice = 119.0,
      estimatedHours = 2.5,
      rating = 4.88f,
      reviewsCount = 142,
      included = listOf(
        "Guided keep/donate/archive decision framework",
        "Packaging of donation items with local donation receipt support",
        "Streamlined seasonal rotation"
      ),
      excluded = listOf("Garment repairs or tailoring alterations"),
      benefits = listOf("Clutter-free peace of mind", "Immediate space reclaimed"),
      beforeSummary = "Overflowing drawers, unworn items taking prime space.",
      afterSummary = "Capsule wardrobe with only items you love and wear regularly."
    ),
    ServiceItem(
      id = "drawer_org",
      name = "Drawer & Dresser Systems",
      category = ServiceCategory.BEDROOM,
      tagline = "Every small piece in its perfect partition",
      description = "Filing method folding and custom honeycomb/acrylic drawer partitions for undergarments, socks, loungewear, and jewelry.",
      startingPrice = 89.0,
      estimatedHours = 2.0,
      rating = 4.92f,
      reviewsCount = 98,
      included = listOf(
        "File-folding techniques for 100% visibility",
        "Installation of modular drawer divider strips",
        "Anti-tangle jewelry tray arrangement"
      ),
      excluded = listOf("Hardware installation requiring power drills"),
      benefits = listOf("Never lose a pair of socks again", "No rummaging required"),
      beforeSummary = "Messy piles where finding one t-shirt disrupts everything.",
      afterSummary = "Neat vertical folds lined up like a showroom catalog."
    ),
    ServiceItem(
      id = "shoe_org",
      name = "Shoe & Footwear Organization",
      category = ServiceCategory.BEDROOM,
      tagline = "Shoe displays that protect and showcase your collection",
      description = "Shoe rack structuring, seasonal shoe boxing with photo/clear labels, boot shaping, and entryway shoe rotation.",
      startingPrice = 95.0,
      estimatedHours = 2.0,
      rating = 4.85f,
      reviewsCount = 76,
      included = listOf("Shoe tree sizing", "Drop-front clear box stacking", "Deodorizing treatment"),
      excluded = listOf("Shoe polishing or leather repair"),
      benefits = listOf("Prevents leather scuffing", "Entryway floor stays clear"),
      beforeSummary = "Tangled shoe piles in bottom of closet and doorway.",
      afterSummary = "Stackable drop-front boxes aligned with sneakers and heels grouped."
    ),
    ServiceItem(
      id = "bed_storage",
      name = "Under-Bed & Blanket Storage",
      category = ServiceCategory.BEDROOM,
      tagline = "Unlock hidden cubic feet of tidy storage",
      description = "Vacuum compression bags, wheeled under-bed totes, guest linen organization, and duvet storage systems.",
      startingPrice = 79.0,
      estimatedHours = 1.5,
      rating = 4.80f,
      reviewsCount = 54,
      included = listOf("Vacuum sealing bulky comforters", "Dust-proof storage totes", "Linen tagging"),
      excluded = listOf("Washing bed linens"),
      benefits = listOf("Frees up prime closet space", "Linen stays fresh for guests"),
      beforeSummary = "Bulky duvets crammed on top shelves falling down.",
      afterSummary = "Flat vacuum-sealed parcels neatly tucked out of view."
    ),

    // Kitchen Services
    ServiceItem(
      id = "kitchen_cabinets",
      name = "Kitchen Cabinets & Zones",
      category = ServiceCategory.KITCHEN,
      tagline = "An intuitive kitchen workflow that makes cooking a joy",
      description = "Ergonomic zoning (Prep, Cook, Serve, Clean), shelf riser placement, pot lid racks, lazy susans, and spice tier setup.",
      startingPrice = 179.0,
      estimatedHours = 4.0,
      rating = 4.97f,
      reviewsCount = 420,
      popular = true,
      colorSeed = 0xFF6C63FF,
      included = listOf(
        "Complete cabinet wipe down & decanting assistance",
        "Zoned workstation layout for quick meal prep",
        "Installation of spice tier racks and lid organizers",
        "Dinnerware and glass stacking optimization"
      ),
      excluded = listOf("Oven deep cleaning or pest extermination"),
      benefits = listOf(
        "Save 15 minutes on every meal prep",
        "Prevent duplicate grocery purchases",
        "Ergonomically accessible heavy pots"
      ),
      beforeSummary = "Avalanche of Tupperware, misplaced baking trays, unreachable bowls.",
      afterSummary = "Zoned gourmet kitchen with turntable spices and vertical pan dividers."
    ),
    ServiceItem(
      id = "pantry_org",
      name = "Pantry & Dry Food Organization",
      category = ServiceCategory.KITCHEN,
      tagline = "Airtight decanting, tiered spices, and zero food waste",
      description = "Airtight container decanting with waterproof labels, expiration date sorting, snack bins, and first-in first-out pantry logic.",
      startingPrice = 159.0,
      estimatedHours = 3.5,
      rating = 4.94f,
      reviewsCount = 310,
      popular = true,
      colorSeed = 0xFF22C55E,
      included = listOf(
        "Decanting into clear airtight canisters",
        "Custom printed pantry labels",
        "Expiration date audit & purge",
        "Tiered can risers and designated snack stations"
      ),
      excluded = listOf("Purchasing food groceries"),
      benefits = listOf("Cut food waste by up to 30%", "Kids can self-serve snacks easily"),
      beforeSummary = "Expired half-open bags of flour, stale chips, hidden canned goods.",
      afterSummary = "Matching glass jars, uniform pantry baskets, clear expiration tags."
    ),
    ServiceItem(
      id = "refrigerator_org",
      name = "Refrigerator & Freezer Revamp",
      category = ServiceCategory.KITCHEN,
      tagline = "Hygienic, zoned cold storage for fresher produce",
      description = "Deep sanitize, produce bin humidity lining, condiment door organization, prepped meal sections, and meat containment.",
      startingPrice = 99.0,
      estimatedHours = 2.0,
      rating = 4.87f,
      reviewsCount = 185,
      included = listOf("Zone assignment for dairy, meats, condiments", "Clear pull-out fridge trays", "Baking soda odor eliminator"),
      excluded = listOf("Defrosting commercial deep freezers"),
      benefits = listOf("Groceries stay fresh 3x longer", "Zero forgotten leftovers rotting in the back"),
      beforeSummary = "Spills on glass shelves, expired salad dressings, hidden vegetables.",
      afterSummary = "Crisp, labeled bins with dedicated 'Eat Me First' zone."
    ),
    ServiceItem(
      id = "utensils_containers",
      name = "Utensils & Container Matching",
      category = ServiceCategory.KITCHEN,
      tagline = "Tame the wild Tupperware drawer forever",
      description = "Lid-to-container matching, elimination of orphan lids, cutlery drawer dividers, knife block reorganization.",
      startingPrice = 75.0,
      estimatedHours = 1.5,
      rating = 4.89f,
      reviewsCount = 112,
      included = listOf("Lid matching audit", "Adjustable bamboo cutlery dividers", "Recycling of damaged plastics"),
      excluded = listOf("Sharpening chef knives"),
      benefits = listOf("No more tumbling containers when opening drawers"),
      beforeSummary = "Tumbled avalanche of mismatched plastic lids and loose spatulas.",
      afterSummary = "Nested containers by shape with dedicated vertical lid organizer."
    ),

    // Storage Services
    ServiceItem(
      id = "store_room",
      name = "Store Room & Utility Reset",
      category = ServiceCategory.STORAGE,
      tagline = "Transform chaos into an inventory-managed hub",
      description = "Heavy-duty rack setup, heavy item floor zoning, clear tote indexing, and utility tool grouping.",
      startingPrice = 189.0,
      estimatedHours = 4.5,
      rating = 4.91f,
      reviewsCount = 168,
      included = listOf("Clear tote labeling with QR inventory code", "Vertical rack stacking", "Hazardous cleaner separation"),
      excluded = listOf("Disposal of hazardous chemical paints"),
      benefits = listOf("Instantly find holiday decor and luggage", "Clean floor perimeter"),
      beforeSummary = "Boxes stacked precariously to ceiling, pathway blocked.",
      afterSummary = "Clear 3-foot walking aisle with labeled 60L bins on metal shelving."
    ),
    ServiceItem(
      id = "garage_org",
      name = "Garage & Tool Organization",
      category = ServiceCategory.STORAGE,
      tagline = "Park your car in your garage once again",
      description = "Wall pegboard design, sports equipment racks, lawn care stations, and overhead storage bin allocation.",
      startingPrice = 249.0,
      estimatedHours = 5.0,
      rating = 4.93f,
      reviewsCount = 124,
      included = listOf("Sports ball bin racks", "Hardware screw/nail sorting in drawers", "Bicycle hook alignment"),
      excluded = listOf("Vehicle mechanical repairs"),
      benefits = listOf("Park vehicle securely inside", "Sports gear grab-and-go"),
      beforeSummary = "Cluttered garden hoses, sports gear on floor, no room for car.",
      afterSummary = "Wall-mounted tools, hanging bikes, clear clean concrete floor."
    ),

    // Kids Services
    ServiceItem(
      id = "kids_toys_books",
      name = "Kids Room & Play Area",
      category = ServiceCategory.KIDS,
      tagline = "Playful systems that make cleanup a fun game",
      description = "Montessori-inspired low-height bins with picture + word labels, Lego color sorting, soft toy hammocks, and accessible book racks.",
      startingPrice = 139.0,
      estimatedHours = 3.0,
      rating = 4.96f,
      reviewsCount = 245,
      popular = true,
      colorSeed = 0xFFF59E0B,
      included = listOf(
        "Visual picture-labels for non-reading children",
        "Low-level bins encouraging independent cleanup",
        "Toy rotation system setup",
        "Book display ledge arrangement"
      ),
      excluded = listOf("Baby sitting or childcare"),
      benefits = listOf(
        "Children learn tidiness habit naturally",
        "No more stepping on painful Lego bricks"
      ),
      beforeSummary = "Toys scattered wall-to-wall, missing puzzle pieces, bedtime stress.",
      afterSummary = "Color-coded fabric bins, designated reading nook, tidy play table."
    ),
    ServiceItem(
      id = "study_desk_org",
      name = "Study Area & School Prep",
      category = ServiceCategory.KIDS,
      tagline = "Distraction-free desk spaces for academic success",
      description = "Desk organization, homework station, notebook filing, art supply sorting, and school backpack hanging hooks.",
      startingPrice = 89.0,
      estimatedHours = 2.0,
      rating = 4.86f,
      reviewsCount = 82,
      included = listOf("Pencil/marker sorting caddies", "Document folder color labeling", "Cable management"),
      excluded = listOf("Academic tutoring"),
      benefits = listOf("Boosts focus and reduces homework friction"),
      beforeSummary = "Pencil shavings, scattered worksheets, tangled charging cords.",
      afterSummary = "Serene desk with pen caddy, labeled subject folders, clean surface."
    ),

    // Living Space
    ServiceItem(
      id = "living_bookshelves",
      name = "Living Room & Bookshelves",
      category = ServiceCategory.LIVING,
      tagline = "Editorial aesthetics that feel cozy and serene",
      description = "Color-gradient or genre bookshelf styling, media console cord hiding, throw pillow and blanket storage baskets, remote control trays.",
      startingPrice = 129.0,
      estimatedHours = 3.0,
      rating = 4.89f,
      reviewsCount = 135,
      included = listOf("Book curation and styling", "Entertainment center cable concealment", "Coffee table minimal styling"),
      excluded = listOf("TV wall mounting"),
      benefits = listOf("Feels like an interior designer staged your home"),
      beforeSummary = "Tangled cords under TV, cluttered bookshelf stacks, loose remotes.",
      afterSummary = "Harmonious bookshelf with art accents, concealed cables, plush throw baskets."
    ),

    // Special
    ServiceItem(
      id = "full_home_transformation",
      name = "Full Home Transformation",
      category = ServiceCategory.SPECIAL,
      tagline = "Our signature multi-organizer whole-home makeover",
      description = "2 to 3 certified professional organizers spend the full day transforming your entire living space: Master Wardrobe, Kitchen, Pantry, Living Room, and Entryway with tailored organizational systems.",
      startingPrice = 499.0,
      estimatedHours = 8.0,
      rating = 5.0f,
      reviewsCount = 512,
      popular = true,
      colorSeed = 0xFF5B5BD6,
      included = listOf(
        "Team of 2-3 Lead Organizers all day",
        "Includes Master Bedroom, Kitchen & Pantry, Living Room",
        "Complementary starter kit of 50 velvet hangers & 12 pantry bins",
        "Personalized maintenance guide & 30-day check-in"
      ),
      excluded = listOf("Moving bulky furniture between floors"),
      benefits = listOf(
        "A total lifestyle reset in just 24 hours",
        "Lasting systems tailored to your family's daily habits"
      ),
      beforeSummary = "Whole home feeling disorganized, clutter in every room, high daily friction.",
      afterSummary = "Pristine, cohesive, magazine-worthy sanctuary where every single item has a home."
    ),
    ServiceItem(
      id = "move_in_unpacking",
      name = "Move-In & Unpacking Setup",
      category = ServiceCategory.SPECIAL,
      tagline = "Unpack and organize from Day One without the boxes",
      description = "Move into your new home with zero stress. We unpack boxes directly into optimized, sustainable organizational systems so you sleep in peace the very first night.",
      startingPrice = 349.0,
      estimatedHours = 6.0,
      rating = 4.96f,
      reviewsCount = 178,
      included = listOf("Unpacking all kitchen and closet boxes", "Arranging optimal zones before items settle into bad spots", "Flattening and bundling cardboard boxes"),
      excluded = listOf("Moving truck transit"),
      benefits = listOf("Live comfortably on night one without living out of boxes for months"),
      beforeSummary = "Towers of taped cardboard boxes throughout the new house.",
      afterSummary = "All boxes unpacked, kitchen ready for cooking, beds made, closets hung."
    )
  )

  val SAMPLE_REVIEWS = listOf(
    CustomerReview(
      id = "rev_1",
      reviewerName = "Amanda Sterling",
      rating = 5f,
      date = "Yesterday",
      comment = "Elena was absolute magic! My closet went from a terrifying avalanche to looking like a luxury boutique in Soho. She didn't judge my mess for a second and taught me vertical folding.",
      tags = listOf("Boutique Finish", "Gentle & Fast", "Space Saver"),
      serviceName = "Wardrobe Organization"
    ),
    CustomerReview(
      id = "rev_2",
      reviewerName = "Marcus Vance",
      rating = 5f,
      date = "3 days ago",
      comment = "The pantry decanting system completely changed how my kids make breakfast. Everything is labeled, tiered, and clear. Worth every single penny!",
      tags = listOf("Kid Friendly", "Pantry Perfection", "No Waste"),
      serviceName = "Pantry & Dry Food"
    ),
    CustomerReview(
      id = "rev_3",
      reviewerName = "Priya Nambiar",
      rating = 5f,
      date = "Last week",
      comment = "Booked the Full Home Transformation after our move. The 2-organizer team arrived with labelers, velvet hangers, and incredible energy. Our home feels like a calm retreat.",
      tags = listOf("Lifesaver", "Professional Team", "10/10"),
      serviceName = "Full Home Transformation"
    )
  )

  val DEFAULT_ORGANIZER = OrganizerProfile(
    id = "org_1",
    name = "Elena Martinez",
    rating = 4.95f,
    completedJobs = 142,
    experienceYears = 4,
    phone = "+1 (555) 234-8901",
    bio = "Certified KonMari Consultant & Senior Home Systems Specialist. Passionate about creating calm, functional living environments tailored to busy family routines.",
    badges = listOf("Top Rated Pro", "Verified Identity", "5-Star Streak", "Master Closet Stylist"),
    skills = listOf("Wardrobe Optimization", "Kitchen Workflows", "Montessori Playrooms", "Decanting Systems", "Minimalist Living"),
    serviceAreas = listOf("Downtown", "Westside", "Greenwood", "Lakeview", "Suburbs")
  )
}
