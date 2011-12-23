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
package org.robobinding.viewattribute.progressbar;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robobinding.viewattribute.AbstractPropertyViewAttribute;
import org.robobinding.viewattribute.AbstractSingleTypeOneWayPropertyAttributeTest;

import android.app.Activity;
import android.widget.ProgressBar;

import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(RobolectricTestRunner.class)
@Ignore
//Un-ignore this when Robolectric supports secondary progress in ShadowProgressBar
public class SecondaryProgressAttributeTest extends AbstractSingleTypeOneWayPropertyAttributeTest<Integer>
{
	private ProgressBar progressBar;
	
	@Before
	public void setUp()
	{
		progressBar = new ProgressBar(new Activity());
	}
	
	@Override
	protected void populateBindingExpectations(BindingSamples<Integer> bindingSamples)
	{
		bindingSamples.add(25, 30);
	}

	@Override
	protected Integer getViewState()
	{
		return progressBar.getSecondaryProgress();
	}

	@Override
	protected AbstractPropertyViewAttribute<Integer> newAttributeInstance(String bindingAttributeValue)
	{
		return new SecondaryProgressAttribute(progressBar, bindingAttributeValue, true);
	}

}