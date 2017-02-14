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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jacorb.JacorbUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CosEventChannelAdmin.EventChannel;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosEventComm.PushConsumer;

import CF.DataType;
import CF.LogEvent;
import CF.LogEventHelper;
import CF.PropertiesHelper;
import CF.PropertyChangeListenerPackage.PropertyChangeEvent;
import CF.PropertyChangeListenerPackage.PropertyChangeEventHelper;
import CF.PropertyChangeListenerPackage.PropertyChangeEventHelper_2_0;
import ExtendedEvent.PropertySetChangeEventType;
import ExtendedEvent.PropertySetChangeEventTypeHelper;
import ExtendedEvent.ResourceStateChangeEventType;
import ExtendedEvent.ResourceStateChangeEventTypeHelper;
import ExtendedEvent.ResourceStateChangeType;
import StandardEvent.AbnormalComponentTerminationEventType;
import StandardEvent.AbnormalComponentTerminationEventTypeHelper;
import StandardEvent.DomainManagementObjectAddedEventType;
import StandardEvent.DomainManagementObjectAddedEventTypeHelper;
import StandardEvent.DomainManagementObjectRemovedEventType;
import StandardEvent.DomainManagementObjectRemovedEventTypeHelper;
import StandardEvent.SourceCategoryType;
import StandardEvent.StateChangeCategoryType;
import StandardEvent.StateChangeEventType;
import StandardEvent.StateChangeEventTypeHelper;
import StandardEvent.StateChangeType;
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
import mil.jpeojtrs.sca.util.AnyUtils;

public class EventViewTest extends UIRuntimeTest {

	private static final String CHANNEL_NAME = "TestChannel";

	private static EventChannel testChannel;
	private static PushConsumer pushConsumer;

	private ORB orb;
	private Set<String> eventFields;

	@BeforeClass
	public static void beforeClass() throws Exception {
		UIRuntimeTest.beforeClass();

		// Standard cleanup from UITest.before()
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		StandardTestActions.cleanup(bot);
		StandardTestActions.switchToScaPerspective(bot);
		bot.sleep(1000);

		// Launch the test domain
		String[] deviceMgrs = new String[] { "DevMgr_localhost" };
		ScaExplorerTestUtils.launchDomainViaWizard(bot, EventViewTest.class.getSimpleName(), deviceMgrs);
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, EventViewTest.class.getSimpleName());
		ScaDomainManagerRegistry registry = ScaPlugin.getDefault().getDomainManagerRegistry(Display.getCurrent());
		ScaDomainManager domMgr = registry.findDomain(EventViewTest.class.getSimpleName());
		Assert.assertNotNull(String.format("Domain %s could not be found", EventViewTest.class.getSimpleName()), domMgr);

		// Create the test event channel
		testChannel = domMgr.eventChannelMgr().createForRegistrations(CHANNEL_NAME);
		pushConsumer = testChannel.for_suppliers().obtain_push_consumer();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		try {
			pushConsumer.disconnect_push_consumer();
		} catch (SystemException e) {
			// PASS
		}
		StandardTestActions.cleanUpLaunches();
		StandardTestActions.cleanUpConnections();
		UIRuntimeTest.afterClass();
	}

	@Before
	@Override
	public void before() throws Exception {
		super.before();
		orb = OrbSession.createSession().getOrb();
		eventFields = new HashSet<>();
	};

	@Override
	public void after() throws CoreException {
		super.after();
	}

	@Test
	public void logEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createLogEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	@Test
	public void domainAddedEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createDomainAddedEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	@Test
	public void domainRemovedEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createDomainRemovedEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	@Test
	public void stateChangeEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createStateChangeEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	@Test
	public void resourceStateChangeEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createResourceStateChangeEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	/**
	 * IDE-1850 Test PropertyChangeEvent from Redhawk 2.0 series
	 */
	@Test
	public void propertyChangeEvent_2_0() throws BadKind, Disconnected {
		openEventView();
		Any event = createPropertyChangeEvent_2_0();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	/**
	 * IDE-1852 Test PropertyChangeEvent from Redhawk 2.1+
	 */
	@Test
	public void propertyChangeEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createPropertyChangeEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	@Test
	public void propertySetChangeEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createPropertySetChangeEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	@Test
	public void abnormalTerminationEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createAbnormalTerminationEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	@Test
	public void messagePortEvent() throws BadKind, Disconnected {
		openEventView();
		Any event = createMessagePortEvent();
		pushConsumer.push(event);
		assertPropertyDetails();
	}

	private void openEventView() {
		// Open the Event View
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { EventViewTest.class.getSimpleName(), "Event Channels" }, CHANNEL_NAME);
		SWTBotTreeItem eventTreeItem = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot,
			new String[] { EventViewTest.class.getSimpleName(), "Event Channels" }, CHANNEL_NAME);
		eventTreeItem.contextMenu("Listen to Event Channel").click();
		bot.viewByTitle(CHANNEL_NAME);
	}

	private void assertPropertyDetails() {
		SWTBotView eventView = ViewUtils.getEventView(bot);
		SWTBotView propView = bot.viewById(ViewUtils.PROPERTIES_VIEW_ID);

		// Get each event with its associated properties
		SWTBotTreeItem[] eventItems = eventView.bot().tree().getAllItems();
		final SWTBotTreeItem eventItem = eventItems[0];
		String type = eventItem.cell(1);

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
		Assert.assertEquals("Incorrect number of properties displayed", eventFields.size() + 3, propTreeItems.length);

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
				Assert.assertTrue(String.format("Property value '%s' was not displayed", propValue), eventFields.contains(propValue));
			}
		}
	}

	/**
	 * IDE-1559 Event viewer redesign
	 * IDE-1743 Disconnect from event channel button
	 */
	@Test
	public void eventViewTest() throws BadKind, Disconnected {
		openEventView();

		// Push multiple events
		List<Any> events = new ArrayList<>();
		events.add(createLogEvent());
		events.add(createDomainAddedEvent());
		events.add(createDomainRemovedEvent());
		for (Any event : events) {
			pushConsumer.push(event);
		}

		assertDetailsButton();

		// IDE-1743 - Test disconnect button
		assertDisconnectButton();
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

	private void assertDisconnectButton() throws Disconnected, BadKind {
		SWTBotView eventView = ViewUtils.getEventView(bot);
		int numOfEvents = eventView.bot().tree().getAllItems().length;
		for (SWTBotToolbarButton button : eventView.getToolbarButtons()) {
			if ("Disconnect".equals(button.getToolTipText())) {
				button.click();
				break;
			}
		}
		pushConsumer.push(createStateChangeEvent());

		Assert.assertEquals("Event Viewer did not disconnect from event channel", numOfEvents, eventView.bot().tree().getAllItems().length);
	}

	private Any createLogEvent() throws BadKind {
		String producerID = "producerId";
		String producerName = "producerName";
		String producerNameFQ = "producerNameFQN";
		long currentTime = System.currentTimeMillis();
		Date date = new Date(currentTime);
		LogLevels level = LogLevels.INFO;
		String logMsg = "Starting Device Manager with /nodes/DevMgr_localhost/DeviceManager.dcd.xml";
		LogEvent logEvent = new LogEvent(producerID, producerName, producerNameFQ, currentTime, level.getLevel(), logMsg);

		Any any = orb.create_any();
		LogEventHelper.insert(any, logEvent);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(producerID);
		eventFields.add(producerName);
		eventFields.add(producerNameFQ);
		eventFields.add(date.toString());
		eventFields.add(level.toString());
		eventFields.add(logMsg);

		return any;
	}

	private Any createDomainAddedEvent() throws BadKind {
		// Assign event props
		String producerId = "producerId";
		String sourceId = "sourceId";
		String sourceName = "sourceName";
		SourceCategoryType sourceCategory = SourceCategoryType.APPLICATION;
		org.omg.CORBA.Object sourceIOR = null; // TODO
		DomainManagementObjectAddedEventType domainAddedEvent = new DomainManagementObjectAddedEventType(producerId, sourceId, sourceName, sourceCategory,
			sourceIOR);

		Any any = orb.create_any();
		DomainManagementObjectAddedEventTypeHelper.insert(any, domainAddedEvent);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(producerId);
		eventFields.add(sourceId);
		eventFields.add(sourceName);
		eventFields.add("APPLICATION");
		eventFields.add("");

		return any;
	}

	private Any createDomainRemovedEvent() throws BadKind {
		// Assign event props
		String producerId = "producerId";
		String sourceId = "sourceId";
		String sourceName = "sourceName";
		SourceCategoryType sourceCategory = SourceCategoryType.APPLICATION;
		DomainManagementObjectRemovedEventType domainRemoveEvent = new DomainManagementObjectRemovedEventType(producerId, sourceId, sourceName, sourceCategory);

		Any any = orb.create_any();
		DomainManagementObjectRemovedEventTypeHelper.insert(any, domainRemoveEvent);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(producerId);
		eventFields.add(sourceId);
		eventFields.add(sourceName);
		eventFields.add("APPLICATION");

		return any;
	}

	private Any createStateChangeEvent() throws BadKind {
		// Assign event props
		String producerId = "producerId";
		String sourceId = "sourceId";
		StateChangeCategoryType stateChangeCategory = StateChangeCategoryType.USAGE_STATE_EVENT;
		StateChangeType stateChangeFrom = StateChangeType.IDLE;
		StateChangeType stateChangeTo = StateChangeType.ACTIVE;
		StateChangeEventType stateChangeEvent = new StateChangeEventType(producerId, sourceId, stateChangeCategory, stateChangeFrom, stateChangeTo);

		Any any = orb.create_any();
		StateChangeEventTypeHelper.insert(any, stateChangeEvent);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(producerId);
		eventFields.add(sourceId);
		eventFields.add("USAGE_STATE_EVENT");
		eventFields.add("IDLE");
		eventFields.add("ACTIVE");

		return any;
	}

	private Any createResourceStateChangeEvent() throws BadKind {
		// Assign event props
		String sourceId = "sourceId";
		String sourceName = "sourceName";
		ResourceStateChangeType stateChangeFrom = ResourceStateChangeType.STOPPED;
		ResourceStateChangeType stateChangeTo = ResourceStateChangeType.STARTED;
		ResourceStateChangeEventType resourceStateChangeEvent = new ResourceStateChangeEventType(sourceId, sourceName, stateChangeFrom, stateChangeTo);

		Any any = orb.create_any();
		ResourceStateChangeEventTypeHelper.insert(any, resourceStateChangeEvent);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(sourceId);
		eventFields.add(sourceName);
		eventFields.add("STOPPED");
		eventFields.add("STARTED");

		return any;
	}

	private Any createPropertyChangeEvent_2_0() throws BadKind {
		List<CF.DataType> structFields = new ArrayList<>();
		String id1 = "stringprop", value1 = "def";
		String id2 = "longprop", value2 = "456";
		structFields.add(new DataType(id1, AnyUtils.stringToAny(value1, "string", false)));
		structFields.add(new DataType(id2, AnyUtils.stringToAny(value2, "long", false)));
		Any structFieldsAny = JacorbUtil.init().create_any();
		PropertiesHelper.insert(structFieldsAny, structFields.toArray(new DataType[structFields.size()]));
		String structId = "testStruct3";
		CF.DataType struct = new CF.DataType(structId, structFieldsAny);

		String evtId = "eventId";
		String regId = "registrationId";
		String resourceId = "resourceId";
		CF.DataType[] properties = new CF.DataType[] { struct };
		PropertyChangeEvent event = new PropertyChangeEvent(evtId, regId, resourceId, properties, null);

		Any retVal = JacorbUtil.init().create_any();
		PropertyChangeEventHelper_2_0.insert(retVal, event);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(evtId);
		eventFields.add(regId);
		eventFields.add(resourceId);
		eventFields.add(String.format("%s = {\n%s = %s\n%s = %s\n}\n", structId, id1, value1, id2, value2));
		eventFields.add("0 (invalid)");

		return retVal;
	}

	private Any createPropertyChangeEvent() throws BadKind {
		List<CF.DataType> structFields = new ArrayList<>();
		String id1 = "stringprop", value1 = "abc";
		String id2 = "longprop", value2 = "123";
		structFields.add(new DataType(id1, AnyUtils.stringToAny(value1, "string", false)));
		structFields.add(new DataType(id2, AnyUtils.stringToAny(value2, "long", false)));
		Any structFieldsAny = JacorbUtil.init().create_any();
		PropertiesHelper.insert(structFieldsAny, structFields.toArray(new DataType[structFields.size()]));
		String structId = "testStruct2";
		CF.DataType struct = new CF.DataType(structId, structFieldsAny);

		String evtId = "eventId";
		String regId = "registrationId";
		String resourceId = "resourceId";
		CF.DataType[] properties = new CF.DataType[] { struct };
		CF.UTCTime timestamp = new CF.UTCTime((short) 1, 1486749720.0, 0.123456);
		PropertyChangeEvent event = new PropertyChangeEvent(evtId, regId, resourceId, properties, timestamp);

		Any retVal = JacorbUtil.init().create_any();
		PropertyChangeEventHelper.insert(retVal, event);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(evtId);
		eventFields.add(regId);
		eventFields.add(resourceId);
		eventFields.add(String.format("%s = {\n%s = %s\n%s = %s\n}\n", structId, id1, value1, id2, value2));
		eventFields.add("1 (valid)");

		return retVal;
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
		testStruct.setId("testStruct1");
		testStruct.setName("testStruct1");
		testStruct.setMode(AccessType.READWRITE);
		ConfigurationKind configKind = PrfFactory.eINSTANCE.createConfigurationKind();
		configKind.setType(StructPropertyConfigurationType.PROPERTY);
		testStruct.getConfigurationKind().add(configKind);
		testStruct.getSimple().add(testSimple);

		CF.DataType testDataType = new CF.DataType(testStruct.getId(), testStruct.toAny());

		String sourceId = "sourceId";
		String sourceName = "sourceName";
		CF.DataType[] properties = new CF.DataType[] { testDataType };
		PropertySetChangeEventType propertySetChangeEvent = new PropertySetChangeEventType(sourceId, sourceName, properties);

		Any any = orb.create_any();
		PropertySetChangeEventTypeHelper.insert(any, propertySetChangeEvent);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(sourceId);
		eventFields.add(sourceName);
		eventFields.add(String.format("%s = {\n%s = %s\n}\n", testStruct.getId(), testSimple.getId(), testSimple.getValue()));

		return any;
	}

	private Any createAbnormalTerminationEvent() throws BadKind {
		// Assign event props
		String deviceId = "deviceId";
		String componentId = "compId";
		String applicationId = "applicationId";
		AbnormalComponentTerminationEventType abnormalTerminationEvent = new AbnormalComponentTerminationEventType(deviceId, componentId, applicationId);

		Any any = orb.create_any();
		AbnormalComponentTerminationEventTypeHelper.insert(any, abnormalTerminationEvent);

		// Add the event to the eventPropMap for a later test step
		eventFields.add(deviceId);
		eventFields.add(componentId);
		eventFields.add(applicationId);

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
		eventFields.add("Hello");
		eventFields.add("World");
		eventFields.add("");

		return messageAny;
	}
}
