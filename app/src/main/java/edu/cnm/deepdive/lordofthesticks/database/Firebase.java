package edu.cnm.deepdive.lordofthesticks.database;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

/**
 * The Firebase class is handling the initiation and connection our firebase database. This class
 * extends LiveData because the database functions off of Documents that are changed and created.
 */
public class Firebase extends LiveData<Task<DocumentSnapshot>> {

  private static final String Log_tag = "FirebaseQueryLiveData";
  private final DocumentReference documentReference;
  private final MyValueEventListener listener = new MyValueEventListener();

  /***
   * The Firebase method is a constructor for the Document needed in Firebase. It has a reference
   * and a listener to the document.
   * @param ref is the Document this class is referencing
   * @param listener is watching for changes in the Document via a snapshot.
   */
  public Firebase(DocumentReference ref, EventListener<DocumentSnapshot> listener) {
    this.documentReference = ref;
    documentReference.addSnapshotListener(listener);
  }

  @Override
  protected void onActive() {
    Log.d(Log_tag, "onActive");
    documentReference.get().addOnCompleteListener(listener);
  }

  @Override
  protected void onInactive() {
    Log.d(Log_tag, "onInactive");
    documentReference.get().addOnCompleteListener(listener);
  }

  private class MyValueEventListener implements OnCompleteListener<DocumentSnapshot> {

    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
      setValue(task);
    }

  }

}

