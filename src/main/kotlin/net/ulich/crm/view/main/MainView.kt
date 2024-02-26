package net.ulich.crm.view.main

import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.router.Route
import io.jmix.flowui.app.main.StandardMainView
import io.jmix.flowui.view.ViewController
import io.jmix.flowui.view.ViewDescriptor

@Route("")
@ViewController("MainView")
@ViewDescriptor("main-view.xml")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
open class MainView : StandardMainView()
