/**
 * Copyright 2011 Cheng Wei, Robert Taylor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.robobinding.viewattribute;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robobinding.attribute.MockValueModelAttributeBuilder.aValueModelAttribute;
import static org.robobinding.viewattribute.MockPropertyViewAttributeConfigBuilder.aPropertyViewAttributeConfig;

import org.junit.Before;
import org.junit.Test;
import org.robobinding.BindingContext;
import org.robobinding.MockBindingContext;
import org.robobinding.presentationmodel.PresentationModelAdapter;
import org.robobinding.property.ValueModel;
import org.robobinding.property.ValueModelUtils;

import android.content.Context;
import android.view.View;

/**
 * 
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
public final class PropertyViewAttributeTest extends ViewAttributeContractTest<PropertyViewAttributeSpy>
{
	private static final String PROPERTY_NAME = "property_name";
	private static final boolean ONE_WAY_BINDING = false;
	private static final boolean TWO_WAY_BINDING = true;
	private static final boolean PRE_INITIALIZE_VIEW = true;
	private static final boolean DONT_PRE_INITIALIZE_VIEW = false;
	private static final int A_NEW_VALUE = 5;
	
	private PropertyViewAttributeSpy attribute;
	private PresentationModelAdapter presentationModelAdapter;
	private Context context;
	private ValueModel<Integer> valueModel = ValueModelUtils.createInteger(-1);
	
	@Before
	public void setUp()
	{
		presentationModelAdapter = mock(PresentationModelAdapter.class);
	}
	
	@Test
	public void givenABoundPropertyViewAttribute_whenValueModelIsUpdated_thenNewValueShouldBePassedToThePropertyViewAttribute()
	{
		setupAndBindAttribute(ONE_WAY_BINDING, DONT_PRE_INITIALIZE_VIEW);
		
		valueModel.setValue(A_NEW_VALUE);
		
		assertThat(attribute.updatedValue, is(A_NEW_VALUE));
	}
	
	@Test
	public void givenAPropertyViewAttributeWithTwoWayBinding_whenTheViewIsUpdated_thenValueModelShouldBeUpdated()
	{
		setupAndBindAttribute(TWO_WAY_BINDING, DONT_PRE_INITIALIZE_VIEW);
		
		attribute.simulateViewUpdate(A_NEW_VALUE);
	
		assertThat(valueModel.getValue(), is(A_NEW_VALUE));
	}
	
	@Test
	public void givenAPropertyViewAttributeWithTwoWayBinding_whenTheViewIsUpdated_thenViewShouldNotReceiveAFurtherUpdate()
	{
		setupAndBindAttribute(TWO_WAY_BINDING, DONT_PRE_INITIALIZE_VIEW);
		
		attribute.simulateViewUpdate(A_NEW_VALUE);
	
		assertThat(attribute.viewUpdateNotificationCount, is(0));
	}
	
	@Test
	public void givenAPropertyViewAttributeWithTwoWayBinding_whenValueModelIsUpdated_thenViewShouldReceiveOnlyASingleUpdate()
	{
		setupAndBindAttribute(TWO_WAY_BINDING, DONT_PRE_INITIALIZE_VIEW);
		
		valueModel.setValue(A_NEW_VALUE);
	
		assertThat(attribute.viewUpdateNotificationCount, is(1));
	}
	
	@Test
	public void givenPreInitializeViewFlag_thenPreInitializeTheViewToReflectTheValueModel()
	{
		setupAndBindAttribute(ONE_WAY_BINDING, PRE_INITIALIZE_VIEW);
		
		assertTrue(attribute.viewInitialized);
	}
	
	@Test
	public void givenNoPreInitializeViewFlag_thenDontPreInitializeTheView()
	{
		setupAndBindAttribute(ONE_WAY_BINDING, DONT_PRE_INITIALIZE_VIEW);
		
		assertFalse(attribute.viewInitialized);
	}
	
	@Test
	public void whenBindAlwaysPreInitializingViewAttributeWithNoPreInitializeViewFlag_thenPreInitializeTheViewToReflectTheValueModel()
	{
		setupAndBindAlwaysPreInitializingViewAttribute(ONE_WAY_BINDING, DONT_PRE_INITIALIZE_VIEW);
		
		assertTrue(attribute.viewInitialized);
	}
	
	@Test
	public void whenBindAlwaysPreInitializingViewAttributeWithPreInitializeViewFlag_thenPreInitializeTheViewToReflectTheValueModelOnceOnly()
	{
		setupAndBindAlwaysPreInitializingViewAttribute(ONE_WAY_BINDING, PRE_INITIALIZE_VIEW);
		
		assertTrue(attribute.viewInitialized);
		assertThat(attribute.viewUpdateNotificationCount, is(1));
	}
	
	private void setupAndBindAttribute(boolean twoWayBinding, boolean preInitializeView)
	{
		attribute = createAttribute(false, twoWayBinding);
		
		bindAttribute(twoWayBinding, preInitializeView);
	}
	
	private void setupAndBindAlwaysPreInitializingViewAttribute(boolean twoWayBinding, boolean preInitializeView)
	{
		attribute = createAttribute(true, twoWayBinding);
		
		bindAttribute(twoWayBinding, preInitializeView);
	}

	private PropertyViewAttributeSpy createAttribute(boolean withAlwaysPreInitializingView, boolean twoWayBinding)
	{
		PropertyViewAttributeSpy viewAttribute = new PropertyViewAttributeSpy(withAlwaysPreInitializingView);
		viewAttribute.initialize(aPropertyViewAttributeConfig(mock(View.class), aValueModelAttribute(PROPERTY_NAME, twoWayBinding)));
		return viewAttribute;
	}

	private void bindAttribute(boolean twoWayBinding, boolean preInitializeView)
	{
		if (twoWayBinding)
			when(presentationModelAdapter.<Integer>getPropertyValueModel(PROPERTY_NAME)).thenReturn(valueModel);
		else
			when(presentationModelAdapter.<Integer>getReadOnlyPropertyValueModel(PROPERTY_NAME)).thenReturn(valueModel);
		
		BindingContext bindingContext = MockBindingContext.create(presentationModelAdapter, context, preInitializeView);
		if(preInitializeView)
		{
			attribute.preInitializeView(bindingContext);
		}
		attribute.bindTo(bindingContext);
	}

	@Override
	protected PropertyViewAttributeSpy throwsExceptionDuringPreInitializingView()
	{
		return createDefaultAttribute();
	}
	
	private PropertyViewAttributeSpy createDefaultAttribute()
	{
		return createAttribute(false, ONE_WAY_BINDING);
	}
	
	@Override
	protected BindingContext bindingContextOfThrowingExceptionDuringBinding()
	{
		return bindingContextOfThrowingExceptionWhenRetrievingPropertyValueModel();
	}
	
	@Override
	protected PropertyViewAttributeSpy throwsExceptionDuringBinding()
	{
		return createDefaultAttribute();
	}

	@Override
	protected BindingContext bindingContextOfThrowingExceptionDuringPreInitializingView()
	{
		return bindingContextOfThrowingExceptionWhenRetrievingPropertyValueModel();
	}
	
	private BindingContext bindingContextOfThrowingExceptionWhenRetrievingPropertyValueModel()
	{
		BindingContext bindingContext = mock(BindingContext.class);
		PresentationModelAdapter presentationModelAdapter = mock(PresentationModelAdapter.class);
		when(bindingContext.getPresentationModelAdapter()).thenReturn(presentationModelAdapter);
	
		when(presentationModelAdapter.getPropertyValueModel(anyString())).thenThrow(new RuntimeException());
		when(presentationModelAdapter.getReadOnlyPropertyValueModel(anyString())).thenThrow(new RuntimeException());
		when(presentationModelAdapter.getDataSetPropertyValueModel(anyString())).thenThrow(new RuntimeException());
		
		return bindingContext;
	}
}