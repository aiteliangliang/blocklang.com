package com.blocklang.marketplace.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ChangelogParseTaskTest {
	
	private MarketplacePublishContext context;

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Before
	public void setup() throws IOException {
		String folder = temp.newFolder().getPath();
		context = new MarketplacePublishContext(folder, "https://github.com/blocklang/blocklang.com.git");
	}

	@Test
	public void run_can_not_null() {
		ChangelogParseTask task = new ChangelogParseTask(context, null);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_can_not_empty() {
		ChangelogParseTask task = new ChangelogParseTask(context, Collections.emptyMap());
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_invalid_key_at_root() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("a", "1");
		changelogMap.put("b", "2");
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_require_id_at_root() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("author", "1");
		changelogMap.put("changes", new ArrayList<Object>());
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_require_author_at_root() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("changes", new ArrayList<Object>());
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_require_changes_at_root() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_id_should_be_string_and_author_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", 1);
		changelogMap.put("author", 2);
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		change1.put("newWidget", new Object());
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_changes_should_be_list() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		changelogMap.put("changes", "I am a String, Not a List");
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_should_has_one_or_more_change() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		changelogMap.put("changes", Collections.emptyList());
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_one_change_should_only_map_to_one_operator() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		change1.put("operator-1", new Object());
		changes.add(change1);
		Map<String, Object> change2 = new HashMap<String, Object>();
		change1.put("operator-2", new Object());
		changes.add(change2);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_should_be_supported_operator() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		change1.put("not-supported-operator", new Object());
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_invalid_keys() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("invalid-key", "a");
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_name_should_be_required_and_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", 1);
		newWidget.put("label", "a");
		newWidget.put("iconClass", "b");
		newWidget.put("appType", new String[] {"web"});
		newWidget.put("properties", Collections.emptyList());
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_label_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", 1);
		newWidget.put("iconClass", "b");
		newWidget.put("appType", new String[] {"web"});
		newWidget.put("properties", Collections.emptyList());
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_iconClass_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", 1);
		newWidget.put("appType", new String[] {"web"});
		newWidget.put("properties", Collections.emptyList());
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_app_type_should_be_string_array() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", "web");
		newWidget.put("properties", Collections.emptyList());
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_app_type_value_should_be_web() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"not-web"}));
		newWidget.put("properties", Collections.emptyList());
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_can_be_null() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isPresent();
	}
	
	@Test
	public void run_changes_new_widget_properties_should_be_list() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", "a");
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_can_be_null() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isPresent();
	}
	
	@Test
	public void run_changes_new_widget_events_should_be_list() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		newWidget.put("events", "a");
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_invalid_keys() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("invalid-property-key", "a");
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_name_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", 1);
		propertyMap.put("label", "a");
		propertyMap.put("value", "b");
		propertyMap.put("valueType", "string");
		propertyMap.put("options", Collections.emptyList());
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_label_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", 1);
		propertyMap.put("value", "b");
		propertyMap.put("valueType", "string");
		propertyMap.put("options", Collections.emptyList());
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_value_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", 1);
		propertyMap.put("valueType", "string");
		propertyMap.put("options", Collections.emptyList());
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_value_type_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", 1);
		propertyMap.put("options", Collections.emptyList());
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_value_type_should_be_the_defined_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "invalid-type");
		propertyMap.put("options", Collections.emptyList());
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_options_can_be_null() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "string");
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isPresent();
	}
	
	@Test
	public void run_changes_new_widget_properties_options_should_be_list() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "string");
		propertyMap.put("options", "not-a-array");
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_options_invalid_key() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "string");
		
		List<Map<String, Object>> options = new ArrayList<Map<String,Object>>();
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("invalid-key", "a");
		options.add(optionMap);
		propertyMap.put("options", options);
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	// value 必填
	@Test
	public void run_changes_new_widget_properties_options_value_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "string");
		
		List<Map<String, Object>> options = new ArrayList<Map<String,Object>>();
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("value", 1);
		options.add(optionMap);
		propertyMap.put("options", options);
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	// label 必填
	@Test
	public void run_changes_new_widget_properties_options_label_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "string");
		
		List<Map<String, Object>> options = new ArrayList<Map<String,Object>>();
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("value", "a");
		optionMap.put("label", 1);
		options.add(optionMap);
		propertyMap.put("options", options);
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_options_title_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "string");
		
		List<Map<String, Object>> options = new ArrayList<Map<String,Object>>();
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("value", "a");
		optionMap.put("label", "b");
		optionMap.put("title", 1);
		options.add(optionMap);
		propertyMap.put("options", options);
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_properties_options_icon_class_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		
		List<Map<String, Object>> properties = new ArrayList<Map<String,Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("name", "a");
		propertyMap.put("label", "b");
		propertyMap.put("value", "c");
		propertyMap.put("valueType", "string");
		
		List<Map<String, Object>> options = new ArrayList<Map<String,Object>>();
		Map<String, Object> optionMap = new HashMap<String, Object>();
		optionMap.put("value", "a");
		optionMap.put("label", "b");
		optionMap.put("iconClass", 1);
		options.add(optionMap);
		propertyMap.put("options", options);
		properties.add(propertyMap);
		newWidget.put("properties", properties);
		newWidget.put("events", Collections.emptyList());
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}

	@Test
	public void run_changes_new_widget_events_invalid_keys() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("invalid-event-key", "a");
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	// name 必填
	@Test
	public void run_changes_new_widget_events_name_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", 1);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	// label 必填
	@Test
	public void run_changes_new_widget_events_label_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", 1);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_value_type_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", 1);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_value_type_can_only_be_function() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "not-function");
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_should_be_list() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		eventMap.put("arguments", "not-a-array");
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_can_be_null() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isPresent();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_keys_should_be_valid() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		
		List<Map<String, Object>> arguments = new ArrayList<Map<String,Object>>();
		Map<String, Object> argumentMap = new HashMap<String, Object>();
		argumentMap.put("invalid-key", "a");
		arguments.add(argumentMap);
		eventMap.put("arguments", arguments);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_name_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		
		List<Map<String, Object>> arguments = new ArrayList<Map<String,Object>>();
		Map<String, Object> argumentMap = new HashMap<String, Object>();
		argumentMap.put("name", 1);
		arguments.add(argumentMap);
		eventMap.put("arguments", arguments);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_label_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		
		List<Map<String, Object>> arguments = new ArrayList<Map<String,Object>>();
		Map<String, Object> argumentMap = new HashMap<String, Object>();
		argumentMap.put("name", "a");
		argumentMap.put("label", 1);
		arguments.add(argumentMap);
		eventMap.put("arguments", arguments);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_value_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		
		List<Map<String, Object>> arguments = new ArrayList<Map<String,Object>>();
		Map<String, Object> argumentMap = new HashMap<String, Object>();
		argumentMap.put("name", "a");
		argumentMap.put("label", "b");
		argumentMap.put("value", 1);
		arguments.add(argumentMap);
		eventMap.put("arguments", arguments);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_value_type_should_be_string() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		
		List<Map<String, Object>> arguments = new ArrayList<Map<String,Object>>();
		Map<String, Object> argumentMap = new HashMap<String, Object>();
		argumentMap.put("name", "a");
		argumentMap.put("label", "b");
		argumentMap.put("value", "c");
		argumentMap.put("valueType", 1);
		arguments.add(argumentMap);
		eventMap.put("arguments", arguments);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}
	
	@Test
	public void run_changes_new_widget_events_arguments_value_type_value_should_be_valid() {
		Map<String, Object> changelogMap = new HashMap<String, Object>();
		changelogMap.put("id", "1");
		changelogMap.put("author", "2");
		
		List<Map<String, Object>> changes = new ArrayList<Map<String,Object>>();
		Map<String, Object> change1 = new HashMap<String, Object>();
		
		Map<String, Object> newWidget = new HashMap<String, Object>();
		newWidget.put("name", "a");
		newWidget.put("label", "b");
		newWidget.put("iconClass", "c");
		newWidget.put("appType", Arrays.asList(new String[] {"web"}));
		newWidget.put("properties", Collections.emptyList());
		
		List<Map<String, Object>> events = new ArrayList<Map<String,Object>>();
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("name", "a");
		eventMap.put("label", "b");
		eventMap.put("valueType", "function");
		
		List<Map<String, Object>> arguments = new ArrayList<Map<String,Object>>();
		Map<String, Object> argumentMap = new HashMap<String, Object>();
		argumentMap.put("name", "a");
		argumentMap.put("label", "b");
		argumentMap.put("value", "c");
		argumentMap.put("valueType", "invalid-value");
		arguments.add(argumentMap);
		eventMap.put("arguments", arguments);
		events.add(eventMap);
		newWidget.put("events", events);
		change1.put("newWidget", newWidget);
		changes.add(change1);
		changelogMap.put("changes", changes);
		
		ChangelogParseTask task = new ChangelogParseTask(context, changelogMap);
		assertThat(task.run()).isEmpty();
	}

	// 上面的用例都是校验
	// 下面的用例都是获取值
	
}
