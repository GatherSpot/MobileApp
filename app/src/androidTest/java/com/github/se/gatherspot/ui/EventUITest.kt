package com.github.se.gatherspot.ui

class EventUITest {
  /*
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    testLogin()
    profileFirebaseConnection.add(Profile.testOrganizer())
  }

  @After
  fun cleanUp() {
    testLoginCleanUp()
  }

  @Test
  fun testEverythingExists() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testParticipant().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.now().plusDays(5),
              eventStartDate = LocalDate.now().plusDays(4),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.now().plusDays(1),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      eventScaffold.assertExists()
      topBar.assertExists()
      backButton.assertExists()

      formColumn.assertExists()
      image.assertExists()
      profileIndicator.assertExists()
      description.assertExists()
      attendeesInfoTitle.assertExists()
      attendeesInfo.assertExists()
      categories.assertExists()
      mapView.assertExists()
      eventDatesTimes.assertExists()
      inscriptionLimitTitle.assertExists()
      inscriptionLimitDateAndTime.assertExists()
      registerButton.assertExists()
    }
  }

  @Test
  fun testEverythingIsDisplayed() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testParticipant().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.now().plusDays(5),
              eventStartDate = LocalDate.now().plusDays(4),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.now().plusDays(1),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      eventScaffold.assertIsDisplayed()
      topBar.assertIsDisplayed()
      backButton.assertIsDisplayed()

      formColumn { assertIsDisplayed() }
      image {
        performScrollTo()
        assertIsDisplayed()
      }
      profileIndicator {
        performScrollTo()
        assertIsDisplayed()
      }
      description {
        performScrollTo()
        assertIsDisplayed()
      }
      attendeesInfoTitle {
        performScrollTo()
        assertIsDisplayed()
      }
      attendeesInfo {
        performScrollTo()
        assertIsDisplayed()
      }
      categories {
        performScrollTo()
        assertIsDisplayed()
      }
      mapView {
        performScrollTo()
        assertIsDisplayed()
      }
      eventDatesTimes {
        performScrollTo()
        assertIsDisplayed()
      }
      inscriptionLimitTitle {
        performScrollTo()
        assertIsDisplayed()
      }
      inscriptionLimitDateAndTime {
        performScrollTo()
        assertIsDisplayed()
      }
      registerButton {
        performScrollTo()
        assertIsDisplayed()
      }
      alertBox { assertIsNotDisplayed() }
    }
  }

  @Test
  fun textsDisplayedAreCorrect() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testParticipant().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf(),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      description {
        assert(
            hasText(
                "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry."))
      }
      profileIndicator {
        hasText("Hosted by")
        hasText("Elias")
      }
      attendeesInfoTitle.assertTextEquals("Number of attendees")
      attendeesInfo {
        hasText("Min: ")
        hasText("Current: ")
        hasText("Max: ")
        hasText("100")
        hasText("10")
        hasText("0")
      }
      categories { hasText("Basketball") }
      eventDatesTimes {
        hasText("Event Start:")
        hasText("Event End:")
        hasText("Apr 14, 2024")
        hasText("Apr 15, 2024")
        hasText("1:00 PM")
        hasText("4:00 PM")
      }
      inscriptionLimitTitle.assertTextContains("Inscription Limit:")
      inscriptionLimitDateAndTime {
        hasText("Apr 11, 2024")
        hasText("11:59 PM")
      }
      registerButton { hasText("Register") }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun registerToAnEventWorks() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description =
                  "Hello: I am a description of the event just saying that I would love to say that Messi is not the best player in the world, but I can't. I am sorry.",
              organizerID = Profile.testParticipant().id,
              attendanceMaxCapacity = 100,
              attendanceMinCapacity = 10,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf(),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        performScrollTo()
        assertIsEnabled()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)
      alertBox {
        assertIsDisplayed()
        hasText("You have been successfully registered!")
      }

      okButton {
        assertExists()
        performClick()
      }
      Thread.sleep(2000)
      registerButton {
        performScrollTo()
        assertIsNotEnabled()
        assert(hasText("Registered"))
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testUnableToRegisterToAFullEvent() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 2,
              attendanceMinCapacity = 1,
              organizerID = Profile.testParticipant().id,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("1", "2"),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        performScrollTo()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)
      alertBox {
        assertIsDisplayed()
        hasText("Event is full")
      }

      okButton.performClick()
      registerButton {
        performScrollTo()
        assertIsNotEnabled()
        assert(hasText("Full"))
      }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testAlreadyRegistered(): Unit = runBlocking {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = Profile.testParticipant().id,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              globalRating = 4,
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf(FirebaseAuth.getInstance().currentUser!!.uid),
              timeBeginning = LocalTime.of(13, 0),
              timeEnding = LocalTime.of(16, 0),
              image = "")
      val eventfirebase = EventFirebaseConnection()
      eventfirebase.add(event)

      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf(FirebaseAuth.getInstance().currentUser!!.uid)),
          EventsViewModel())
    }
    Thread.sleep(3000)
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      registerButton {
        performScrollTo()
        assertIsNotEnabled()
        assert(hasText("Registered"))
      }
    }
  }

  @Test
  fun testOrganiserDeleteEditButtonAreHere() {
    melvinLogin()
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = ProfileFirebaseConnection().getCurrentUserUid()!!,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("TEST"),
              timeBeginning = LocalTime.of(13, 0),
              globalRating = 4,
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      editEventButton { assertIsDisplayed() }
      deleteButton { assertIsDisplayed() }
    }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testClickOnDeleteButton() {
    melvinLogin()
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = ProfileFirebaseConnection().getCurrentUserUid()!!,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("TEST"),
              timeBeginning = LocalTime.of(13, 0),
              globalRating = 4,
              timeEnding = LocalTime.of(16, 0),
              image = "")

      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      editEventButton { assertIsDisplayed() }
      deleteButton {
        assertIsDisplayed()
        performClick()
      }
      composeTestRule.waitUntilAtLeastOneExists(hasTestTag("alertBox"), 6000)
      alertBox {
        assertIsDisplayed()
        hasText("Are you sure you want to delete this event? This action cannot be undone.")
      }
      okButton {
        assertIsDisplayed()
        hasText("Delete")
      }
      cancelButton.performClick()
      alertBox { assertIsNotDisplayed() }
    }
  }

  @Test
  fun testProfileIsCorrectlyFetched() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val event =
          Event(
              id = "1",
              title = "Event Title",
              description = "Hello: I am a description",
              attendanceMaxCapacity = 10,
              attendanceMinCapacity = 1,
              organizerID = Profile.testOrganizer().id,
              categories = setOf(Interests.BASKETBALL),
              eventEndDate = LocalDate.of(2024, 4, 15),
              eventStartDate = LocalDate.of(2024, 4, 14),
              inscriptionLimitDate = LocalDate.of(2024, 4, 11),
              inscriptionLimitTime = LocalTime.of(23, 59),
              location = null,
              registeredUsers = mutableListOf("TEST"),
              timeBeginning = LocalTime.of(13, 0),
              globalRating = 4,
              timeEnding = LocalTime.of(16, 0),
              image = "EventUITestImage")


      EventUI(
          event,
          NavigationActions(navController),
          EventRegistrationViewModel(listOf()),
          EventsViewModel())
    }
    ComposeScreen.onComposeScreen<EventUIScreen>(composeTestRule) {
      profileIndicator.assertIsDisplayed()
      userName { hasText("John Doe") }
      // profileIndicator.performClick()
    }
  }
  // write an integration test that tests the following:
  // Start from Events screen

     */
}
