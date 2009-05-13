package com.aptana.rdt.internal.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.rdt.core.gems.Gem;
import com.aptana.rdt.core.gems.GemListener;
import com.aptana.rdt.core.gems.IGemManager;
import com.aptana.rdt.ui.gems.GemsView;
import com.aptana.rdt.ui.gems.InstallGemDialog;

/**
 * Install a gem
 * 
 * @author cwilliams
 */
public class InstallGemActionDelegate implements IObjectActionDelegate, IViewActionDelegate, GemListener
{

	private IAction action;
	private GemsView gemsView;
	private IGemManager gemManager;

	public InstallGemActionDelegate()
	{
	}

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		this.action = action;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		InstallGemDialog dialog = new InstallGemDialog(Display.getCurrent().getActiveShell());
		if (dialog.open() == Dialog.OK)
		{
			final Gem gem = dialog.getGem();
			final String sourceURL = dialog.getSourceURL();
			UIJob job = new UIJob("Installing gem " + gem.getName())
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					getGemManager().installGem(gem, sourceURL);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.action = action;
		if (getGemManager() != this.gemManager)
		{
			this.gemManager.removeGemListener(this);
			getGemManager().addGemListener(this);
			this.gemManager = getGemManager();
		}
		action.setEnabled(isEnabled());
	}

	private boolean isEnabled()
	{
		return getGemManager().isRubyGemsInstalled() && getGemManager().isInitialized();
	}

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
		this.gemsView = (GemsView) view;
		this.gemManager = getGemManager();
		getGemManager().addGemListener(this);
	}

	private IGemManager getGemManager()
	{
		return gemsView.getGemManager();
	}

	public void gemAdded(Gem gem)
	{
	}

	public void gemRemoved(Gem gem)
	{
	}

	public void gemsRefreshed()
	{
	}

	public void managerInitialized()
	{
		if (action == null)
			return;
		action.setEnabled(isEnabled());
	}
}
