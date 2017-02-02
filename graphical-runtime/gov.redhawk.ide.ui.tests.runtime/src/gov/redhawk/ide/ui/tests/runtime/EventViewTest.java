/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CosEventChannelAdmin.EventChannel;
import org.omg.CosEventComm.Disconnected;

import CF.LogEvent;
import CF.LogEventHelper;
import CF.EventChannelManagerPackage.ChannelAlreadyExists;
import CF.EventChannelManagerPackage.OperationFailed;
import CF.EventChannelManagerPackage.OperationNotAllowed;
import CF.EventChannelManagerPackage.ServiceUnavailable;
import ExtendedEvent.PropertySetChangeEventType;
import ExtendedEvent.PropertySetChangeEventTypeHelper;
import ExtendedEvent.ResourceStateChangeEventType;
import ExtendedEvent.ResourceStateChangeEventTypeHelper;
import StandardEvent.AbnormalComponentTerminationEventType;
import StandardEvent.AbnormalComponentTerminationEventTypeHelper;
import StandardEvent.DomainManagementObjectAddedEventType;
import StandardEvent.DomainManagementObjectAddedEventTypeHelper;
import StandardEvent.DomainManagementObjectRemovedEventType;
import StandardEvent.DomainManagementObjectRemovedEventTypeHelper;
import StandardEvent.StateChangeEventType;
import StandardEvent.StateChangeEventTypeHelper;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.logging.ui.LogLevels;
import gov.redhawk.model.sca.ScaDomainManager;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.sca.ScaPlugin;
import gov.redhawk.sca.util.OrbSession;
import mil.jpeojtrs.sca.prf.AccessType;
import mil.jpeojtrs.sca.prf.Action;
import mil.jpeojtrs.sca.prf.ActionType;
import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.PropertyValueType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructPropertyConfigurationType;

public class EventViewTest extends UIRuntimeTest {

	private static final String CHANNEL_NAME = "TestChannel";
	private ORB orb;
	private Map<String, List<String>> eventPropMap;

	@Before
	@Override
	public void before() throws Exception {
		super.before();
		orb = OrbSession.createSession().getOrb();
		eventPropMap = new HashMap<String, List<String>>();
	};

	@After
	public void clean() throws DebugException {
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
	}

	@Test
	public void eventViewTest() throws ChannelAlreadyExists, OperationNotAllowed, OperationFailed, ServiceUnavailable, Disconnected, BadKind {
		// Launch the test domain
		String domainName = "EventViewDomain";
		String[] deviceMgrs = new String[] { "DevMgr_localhost" };
		ScaExplorerTestUtils.launchDomainViaWizard(bot, domainName, deviceMgrs);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, domainName);
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(Display.getCurrent());
		ScaDomainManager domMgr = registry.findDomain(domainName);
		Assert.assertNotNull(String.format("Domain %s could not be found", domainName), domMgr);

		// Create the test event channel
		EventChannel testChannel = domMgr.eventChannelMgr().createForRegistrations(CHANNEL_NAME);

		// Open the Event View
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { domainName, "Event Channels" }, CHANNEL_NAME);
		SWTBotTreeItem eventTreeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, new String[] { domainName, "Event Channels" }, CHANNEL_NAME);
		eventTreeItem.contextMenu("Listen to Event Channel").click();

		// Make a new method for returning all events to be tested. Map event ID to expected properties.
		List<Any> events = createEvents();

		// Push all events
		for (Any event : events) {
			testChannel.for_suppliers().obtain_push_consumer().push(event);
		}

		// Check that the property view displays the events and all associated properties
		assertPropertyDetails();

		assertDetailsButton();

		// IDE-1743 - Test disconnect button
		SWTBotView eventView = ViewUtils.getEventView(bot);
		int numOfEvents = eventView.bot().tree().getAllItems().length;
		for (SWTBotToolbarButton button : eventView.getToolbarButtons()) {
			if ("Disconnect".equals(button.getToolTipText())) {
				button.click();
				break;
			}
		}
		testChannel.for_suppliers().obtain_push_consumer().push(events.get(0));

		Assert.assertEquals("Event Viewer did not disconnect from event channel", numOfEvents, eventView.bot().tree().getAllItems().length);
	}

	private void assertDetailsButton() {
		SWTBotView eventView = ViewUtils.getEventView(bot);
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
		propView.close();
		try {
			bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
			Assert.fail("Property view did not close");
		} catch (WidgetNotFoundException e) {
			// PASS
		}

		eventView.bot().tree().select(0);
		for (SWTBotToolbarButton button : eventView.getToolbarButtons()) {
			if ("Show Details".equals(button.getToolTipText())) {
				button.click();
				break;
			}
		}
		bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);
	}

	private void assertPropertyDetails() {
		SWTBotView eventView = ViewUtils.getEventView(bot);
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);

		// Get each event with its associated properties
		SWTBotTreeItem[] eventItems = eventView.bot().tree().getAllItems();
		for (final SWTBotTreeItem eventItem : eventItems) {
			String type = eventItem.cell(1);
			List<String> eventProperties = eventPropMap.get(type);
			Assert.assertNotNull(String.format("Could not find properties associated with the %s event", type), eventProperties);

			// Check that expected properties show in the PropertiesView
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					eventItem.select();
					return eventItem.isSelected();
				}

				@Override
				public String getFailureMessage() {
					return "Event item selection failed";
				}
			});

			SWTBotTreeItem[] propTreeItems = propView.bot().tree().getAllItems();
			if ("MessageEvent".equals(type)) {
				type = CF.PropertiesHelper.id();
				List<SWTBotTreeItem> allItems = new ArrayList<SWTBotTreeItem>(Arrays.asList(propTreeItems));
				for (SWTBotTreeItem treeItem : propTreeItems) {
					treeItem.expand();
					allItems.addAll(Arrays.asList(treeItem.getItems()));
				}
				propTreeItems = allItems.toArray(new SWTBotTreeItem[0]);
			}

			// +3 to account for Timestamp, Channel, Type generic properties
			Assert.assertEquals("Incorrect number of properties displayed", eventProperties.size() + 3, propTreeItems.length);

			// Assert prop values are correctly displayed
			for (SWTBotTreeItem propItem : propTreeItems) {
				String propLabel = propItem.cell(0);
				String propValue = propItem.cell(1);

				if ("Timestamp".equals(propLabel)) {
					continue; // Don't bother checking timestamp
				} else if ("Event Channel".equals(propLabel)) {
					Assert.assertEquals("Incorrect channel value", CHANNEL_NAME, propValue);
				} else if ("Event Type".equals(propLabel)) {
					Assert.assertTrue("Incorrect Event Type value", propValue.matches(".*" + type + ".*"));
				} else {
					// Check event specific properties
					Assert.assertTrue(String.format("Property %s was not displayed", propValue), eventProperties.contains(propValue));
				}
			}
		}
	}

	// Create an event for every event type the IDE supports
	private List<Any> createEvents() throws BadKind {
		List<Any> events = new ArrayList<Any>();

		events.add(createDomainAddedEvent());
		events.add(createDomainRemovedEvent());
		events.add(createStateChangeEvent());
		events.add(createResourceStateChangeEvent());
		events.add(createPropertySetChangeEvent());
		events.add(createMessagePortEvent());
		events.add(createAbnormalTerminationEvent());
		events.add(createLogEvent());

		return events;
	}

	private Any createLogEvent() throws BadKind {
		long currentTime = System.currentTimeMillis();
		Date date = new Date(currentTime);
		LogLevels level = LogLevels.INFO;
		String[] eventProps = { "producerId", "producerName", "producerNameFQN", date.toString(), level.getLabel(), "I am a logging message" };
		LogEvent logEvent = new LogEvent(eventProps[0], eventProps[1], eventProps[2], currentTime, level.getLevel(), eventProps[5]);

		Any any = orb.create_any();
		LogEventHelper.insert(any, logEvent);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put(LogEventHelper.type().name(), Arrays.asList(eventProps));

		return any;
	}

	private Any createDomainAddedEvent() throws BadKind {
		// Assign event props - TODO: How to make a SourceIOR object?
		String[] eventProps = { "producerId", "sourceId", "sourceName", "APPLICATION", "" };
		DomainManagementObjectAddedEventType domainAddedEvent = new DomainManagementObjectAddedEventType(eventProps[0], eventProps[1], eventProps[2],
			StandardEvent.SourceCategoryType.APPLICATION, null);

		Any any = orb.create_any();
		DomainManagementObjectAddedEventTypeHelper.insert(any, domainAddedEvent);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put(DomainManagementObjectAddedEventTypeHelper.type().name(), Arrays.asList(eventProps));

		return any;
	}

	private Any createDomainRemovedEvent() throws BadKind {
		// Assign event props
		String[] eventProps = { "producerId", "sourceId", "sourceName", "APPLICATION" };
		DomainManagementObjectRemovedEventType domainRemoveEvent = new DomainManagementObjectRemovedEventType(eventProps[0], eventProps[1], eventProps[2],
			StandardEvent.SourceCategoryType.APPLICATION);

		Any any = orb.create_any();
		DomainManagementObjectRemovedEventTypeHelper.insert(any, domainRemoveEvent);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put(DomainManagementObjectRemovedEventTypeHelper.type().name(), Arrays.asList(eventProps));

		return any;
	}

	private Any createStateChangeEvent() throws BadKind {
		// Assign event props
		String[] eventProps = { "producerId", "sourceId", "USAGE_STATE_EVENT", "IDLE", "ACTIVE" };
		StateChangeEventType stateChangeEvent = new StateChangeEventType(eventProps[0], eventProps[1], StandardEvent.StateChangeCategoryType.USAGE_STATE_EVENT,
			StandardEvent.StateChangeType.IDLE, StandardEvent.StateChangeType.ACTIVE);

		Any any = orb.create_any();
		StateChangeEventTypeHelper.insert(any, stateChangeEvent);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put(StateChangeEventTypeHelper.type().name(), Arrays.asList(eventProps));

		return any;
	}

	private Any createResourceStateChangeEvent() throws BadKind {
		// Assign event props
		String[] eventProps = { "sourceId", "sourceName", "STOPPED", "STARTED" };
		ResourceStateChangeEventType resourceStateChangeEvent = new ResourceStateChangeEventType(eventProps[0], eventProps[1],
			ExtendedEvent.ResourceStateChangeType.STOPPED, ExtendedEvent.ResourceStateChangeType.STARTED);

		Any any = orb.create_any();
		ResourceStateChangeEventTypeHelper.insert(any, resourceStateChangeEvent);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put(ResourceStateChangeEventTypeHelper.type().name(), Arrays.asList(eventProps));

		return any;
	}

	private Any createPropertySetChangeEvent() throws BadKind {
		// Assign event props
		Simple testSimple = PrfFactory.eINSTANCE.createSimple();
		testSimple.setId("testSimple");
		testSimple.setName("testSimple");
		testSimple.setMode(AccessType.READWRITE);
		testSimple.setType(PropertyValueType.STRING);

		Kind testKind = PrfFactory.eINSTANCE.createKind();
		testKind.setType(PropertyConfigurationType.PROPERTY);
		testSimple.getKind().add(testKind);

		Action testAction = PrfFactory.eINSTANCE.createAction();
		testAction.setType(ActionType.EXTERNAL);
		testSimple.setAction(testAction);

		testSimple.setValue("testValue");

		Struct testStruct = PrfFactory.eINSTANCE.createStruct();
		testStruct.setId("testStruct");
		testStruct.setName("testStruct");
		testStruct.setMode(AccessType.READWRITE);
		ConfigurationKind configKind = PrfFactory.eINSTANCE.createConfigurationKind();
		configKind.setType(StructPropertyConfigurationType.PROPERTY);
		testStruct.getConfigurationKind().add(configKind);
		testStruct.getSimple().add(testSimple);

		CF.DataType testDataType = new CF.DataType(testStruct.getId(), testStruct.toAny());
		String structDetailString = String.format("%s = {\n%s = %s\n}\n", testStruct.getId(), testSimple.getId(), testSimple.getValue());
		String[] eventProps = { "sourceId", "sourceName", structDetailString };
		PropertySetChangeEventType propertySetChangeEvent = new PropertySetChangeEventType(eventProps[0], eventProps[1], new CF.DataType[] { testDataType });

		Any any = orb.create_any();
		PropertySetChangeEventTypeHelper.insert(any, propertySetChangeEvent);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put(PropertySetChangeEventTypeHelper.type().name(), Arrays.asList(eventProps));

		return any;
	}

	private Any createAbnormalTerminationEvent() throws BadKind {
		// Assign event props
		String[] eventProps = { "deviceId", "compId", "applicationId" };
		AbnormalComponentTerminationEventType abnormalTerminationEvent = new AbnormalComponentTerminationEventType(eventProps[0], eventProps[1], eventProps[2]);

		Any any = orb.create_any();
		AbnormalComponentTerminationEventTypeHelper.insert(any, abnormalTerminationEvent);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put(AbnormalComponentTerminationEventTypeHelper.type().name(), Arrays.asList(eventProps));

		return any;
	}

	private Any createMessagePortEvent() throws BadKind {
		String[] eventProps = { "Hello", "World", "" };

		Any value1 = orb.create_any();
		value1.insert_string(eventProps[0]);
		Any value2 = orb.create_any();
		value2.insert_string(eventProps[1]);

		CF.DataType[] details = { new CF.DataType("cell1", value1), new CF.DataType("cell2", value2) };
		Any detailsAny = orb.create_any();
		CF.PropertiesHelper.insert(detailsAny, details);

		CF.DataType[] message = { new CF.DataType("testMessage", detailsAny) };

		Any messageAny = orb.create_any();
		CF.PropertiesHelper.insert(messageAny, message);

		// Add the event to the eventPropMap for a later test step
		eventPropMap.put("MessageEvent", Arrays.asList(eventProps));

		return messageAny;
	}

}
