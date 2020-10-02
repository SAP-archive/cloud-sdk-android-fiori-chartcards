package com.example.fiorichartcardsapp.util

import com.example.fiorichartcardsapp.repos.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Sets the main coroutines dispatcher to a [TestCoroutineScope] for unit testing. A
 * [TestCoroutineScope] provides control over the execution of coroutines.
 *
 * Declare it as a JUnit Rule:
 *
 * ```
 * @get:Rule
 * var testCoroutineRule = TestCoroutineScopeRule()
 * ```
 *
 * Use it directly as a [TestCoroutineScope]:
 *
 * ```
 * testCoroutineRule.pauseDispatcher()
 * ...
 * testCoroutineRule.resumeDispatcher()
 * ...
 * testCoroutineRule.runBlockingTest { }
 * ...
 *
 * ```
 */
@ExperimentalCoroutinesApi
class TestCoroutineScopeRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) :
    TestWatcher(),
    TestCoroutineScope by TestCoroutineScope(dispatcher) {

    val testDispatcherProvider = object : DispatcherProvider {
        override fun default(): CoroutineDispatcher = dispatcher
        override fun io(): CoroutineDispatcher = dispatcher
        override fun main(): CoroutineDispatcher = dispatcher
        override fun unconfined(): CoroutineDispatcher = dispatcher
    }

    override fun starting(description: Description?) {
        super.starting(description)
        // If your codebase allows the injection of other dispatchers like
        // Dispatchers.Default and Dispatchers.IO, consider injecting all of them here
        // and renaming this class to `CoroutineScopeRule`
        //
        // All injected dispatchers in a test should point to a single instance of
        // TestCoroutineDispatcher.
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}