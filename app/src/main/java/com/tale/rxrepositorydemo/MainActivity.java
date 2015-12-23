package com.tale.rxrepositorydemo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.tale.rxrepository.CloudProvider;
import com.tale.rxrepository.DiskProvider;
import com.tale.rxrepository.Repository;
import java.util.Comparator;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private Repository<Integer> repository;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        load();
      }
    });
    repository = new Repository<>(new DiskProvider<Integer>() {
      Integer data;

      @Override public Observable<Integer> save(Integer data) {
        this.data = data;
        return get();
      }

      @Override public Observable<Integer> get() {
        return Observable.defer(new Func0<Observable<Integer>>() {
          @Override public Observable<Integer> call() {
            SystemClock.sleep(500);
            return Observable.just(data).compose(logSource("DISK"));
          }
        });
      }
    }, new CloudProvider<Integer>() {
      @Override public Observable<Integer> get() {
        return Observable.defer(new Func0<Observable<Integer>>() {
          @Override public Observable<Integer> call() {
            SystemClock.sleep(2000);
            return Observable.just(2000).compose(logSource("CLOUD"));
          }
        });
      }
    }, new Comparator<Integer>() {
      @Override public int compare(Integer lhs, Integer rhs) {
        if (lhs == null) {
          if (rhs == null) {
            return 0;
          }
          return 1;
        } else if (rhs == null) {
          return -1;
        }
        return lhs - rhs;
      }
    });
  }

  @NonNull private Observable.Transformer<Integer, Integer> logSource(final String source) {
    return new Observable.Transformer<Integer, Integer>() {
      @Override public Observable<Integer> call(Observable<Integer> integerObservable) {
        return integerObservable.doOnNext(new Action1<Integer>() {
          @Override public void call(Integer integer) {
            Log.d(TAG, source + " called with: " + "data = [" + integer + "]");
          }
        });
      }
    };
  }

  private void load() {
    repository.get()
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Integer>() {
          @Override public void onCompleted() {
            Log.d(TAG, "onCompleted() called with: " + "");
          }

          @Override public void onError(Throwable e) {
            Log.e(TAG, "onError: ", e);
          }

          @Override public void onNext(Integer integer) {
            Log.d(TAG, "onNext() called with: " + "integer = [" + integer + "]");
          }
        });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
