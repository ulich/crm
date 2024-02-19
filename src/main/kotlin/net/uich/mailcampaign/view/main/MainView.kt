package net.uich.mailcampaign.view.main

import com.vaadin.flow.router.Route
import io.jmix.flowui.app.main.StandardMainView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor

@Route("")
@ViewController("MainView")
@ViewDescriptor("main-view.xml")
open class MainView : StandardMainView()
