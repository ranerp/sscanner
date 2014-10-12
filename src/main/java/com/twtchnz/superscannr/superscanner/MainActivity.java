package com.twtchnz.superscannr.superscanner;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.twtchnz.superscannr.superscanner.adapters.CustomViewPager;
import com.twtchnz.superscannr.superscanner.adapters.SectionPagerAdapter;
import com.twtchnz.superscannr.superscanner.fragments.*;
import com.twtchnz.superscannr.superscanner.resources.ResourceManager;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ActionBar actionBar;

    ResourceManager resourceManager;

    SectionPagerAdapter sectionPagerAdapter;

    CustomViewPager viewPager;

    OrderArchiveFragment orderArchiveFragment;
    OrderTemplateFragment orderTemplateFragment;
    EmailTemplateFragment emailTemplateFragment;

    FirstMenuFragment mainFragment;

    ScanFragment scanFragment;

    ActiveOrderHeaderFragment activeOrderHeaderFragment;
    ActiveOrderFooterFragment activeOrderFooterFragment;

    FileFragment fileFragment;

    SendEmailFragment sendEmailFragment;

    boolean isDummyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startApp();
    }

    private void startApp() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(Utils.START_APP_MESSAGE);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        resourceManager = new ResourceManager(this);
        if (!resourceManager.init()) {

            Toast.makeText(this, Utils.RES_DB_OR_FILE_ERROR, Toast.LENGTH_LONG).show();

        } else {
            isDummyOrder = resourceManager.isDummyOrder();

            orderArchiveFragment = new OrderArchiveFragment(resourceManager);
            orderTemplateFragment = new OrderTemplateFragment(resourceManager);
            emailTemplateFragment = new EmailTemplateFragment(resourceManager);
            mainFragment = new FirstMenuFragment(resourceManager);
            scanFragment = new ScanFragment(resourceManager);
            activeOrderHeaderFragment = new ActiveOrderHeaderFragment(resourceManager);
            activeOrderFooterFragment = new ActiveOrderFooterFragment(resourceManager);
            fileFragment = new FileFragment(resourceManager);
            sendEmailFragment = new SendEmailFragment(resourceManager);


            ArrayList<Fragment> fragments = new ArrayList<Fragment>();
            fragments.add(orderArchiveFragment);
            fragments.add(orderTemplateFragment);
            fragments.add(emailTemplateFragment);
            fragments.add(mainFragment);
            fragments.add(scanFragment);
            fragments.add(activeOrderHeaderFragment);
            fragments.add(activeOrderFooterFragment);
            fragments.add(fileFragment);
            fragments.add(sendEmailFragment);

            sectionPagerAdapter = new SectionPagerAdapter(getFragmentManager(), fragments, fragments.indexOf(mainFragment));
            setPagerAdapterScrollLimit();

            viewPager = (CustomViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(sectionPagerAdapter);

            actionBar = getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {

                @Override
                public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                }
            };

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_order_archive)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_order_info_template)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_email_template)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_main)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_scan_items)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_active_order_info)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_active_order_footer)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_file)).setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_send_email)).setTabListener(tabListener));

            viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    getActionBar().setSelectedNavigationItem(position);
                }
            });

            viewPager.setCurrentItem(fragments.indexOf(mainFragment));
        }

        dialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       getMenuInflater().inflate(R.menu.menu, menu);
       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPagerAdapterScrollLimit() {

        isDummyOrder = resourceManager.isDummyOrder();
        if(isDummyOrder)
            sectionPagerAdapter.setCount(sectionPagerAdapter.getMainFragmentPosition() + 1);
        else
            sectionPagerAdapter.setCount(sectionPagerAdapter.getFragmentsSize());
    }

    public void goToMainPage() {
        viewPager.setCurrentItem(sectionPagerAdapter.getMainFragmentPosition());
    }

    public void onItemEditClicked(View view) {
        scanFragment.onItemEditClicked(view);
    }

    public void onOrderNumberToggleClicked(View view) { scanFragment.onOrderNumberToggleClicked(view); }

    public void onMaterialCodeToggleClicked(View view) {
        scanFragment.onMaterialCodeToggleClicked(view);
    }

    public void onSalesOrderToggleClicked(View view) {
        scanFragment.onSalesOrderToggleClicked(view);
    }

    public void onScanRowSwitchClicked(View view) {
        scanFragment.onScanRowSwitchClicked(view);
    }

    public void onSwitchClick(View view) {
        mainFragment.onSwitchClick(view);
    }

    public void onOrderArchiveButtonClicked(View view) {
        int toPosition = sectionPagerAdapter.getFragmentPosition(orderArchiveFragment);
        viewPager.setCurrentItem(toPosition);
    }

    public void onOrderTemplateButtonClicked(View view) {
        int toPosition = sectionPagerAdapter.getFragmentPosition(orderTemplateFragment);
        viewPager.setCurrentItem(toPosition);
    }

    public void onEmailTemplateButtonClicked(View view) {
        int toPosition = sectionPagerAdapter.getFragmentPosition(emailTemplateFragment);
        viewPager.setCurrentItem(toPosition);
    }

    public void onNewOrderClicked(View view) {
        resourceManager.activateNewOrder();

        mainFragment.onNewOrderClicked(view);

        setPagerAdapterScrollLimit();

        Toast.makeText(this, R.string.new_order_made_message, Toast.LENGTH_SHORT).show();
    }

    public void onOrderArchiveRowSwitchClicked(View view) {
        orderArchiveFragment.onOrderArchiveRowSwitchClicked(view);

    }

    public void onOrderArchiveActivateClicked(View view) {
        orderArchiveFragment.onOrderArchiveActivateClicked(view);

        viewPager.setCurrentItem(sectionPagerAdapter.getMainFragmentPosition());

        setPagerAdapterScrollLimit();

        Toast.makeText(this, R.string.order_activated_message, Toast.LENGTH_SHORT).show();
    }

    public void onDeleteOrderClicked(View view) {
        mainFragment.onDeleteOrderClicked(view);

        setPagerAdapterScrollLimit();
    }

    public void onPdfCreateClicked(View view) {
        fileFragment.onPdfCreateClicked(view);

        sendEmailFragment.setViews();

        Toast.makeText(this, R.string.pdf_create_message, Toast.LENGTH_SHORT).show();
    }

    public void onPdfViewClicked(View view) {
        viewPager.setPagerEnabled(false);
        fileFragment.onPdfViewClicked(view);
    }

    public void onPdfCloseClicked(View view) {
        viewPager.setPagerEnabled(true);
        fileFragment.onPdfCloseClicked(view);
    }

    public void onXlsCreateClicked(View view) {
        fileFragment.onXlsCreateClicked(view);

        sendEmailFragment.setViews();

        Toast.makeText(this, getString(R.string.xls_create_message), Toast.LENGTH_SHORT).show();
    }

    public void onXlsViewClicked(View view) {
        viewPager.setPagerEnabled(false);
        fileFragment.onXlsViewClicked(view);
    }

    public void onXlsCloseClicked(View view) {
        viewPager.setPagerEnabled(true);
        fileFragment.onXlsCloseClicked(view);
    }
}
