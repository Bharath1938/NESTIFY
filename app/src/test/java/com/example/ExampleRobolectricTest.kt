package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CatalogData
import com.example.data.model.ServiceCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nestify", appName)
  }

  @Test
  fun `catalog services covers all categories`() {
    val services = CatalogData.SERVICES
    assertTrue(services.isNotEmpty())
    val categories = services.map { it.category }.toSet()
    assertTrue(categories.contains(ServiceCategory.BEDROOM))
    assertTrue(categories.contains(ServiceCategory.KITCHEN))
    assertTrue(categories.contains(ServiceCategory.STORAGE))
    assertTrue(categories.contains(ServiceCategory.KIDS))
    assertTrue(categories.contains(ServiceCategory.LIVING))
    assertTrue(categories.contains(ServiceCategory.SPECIAL))
  }
}
