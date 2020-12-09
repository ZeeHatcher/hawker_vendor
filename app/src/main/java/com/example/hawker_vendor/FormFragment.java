package com.example.hawker_vendor;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "Form";
    private static final String ARG_ITEM = "item";
    private static final int REQUEST_CODE = 1;

    private FirebaseAuth auth;
    private FirestoreHandler handler;
    private FirebaseStorage storage;

    private boolean isEdit;
    private Uri imageUri;
    private Item item;

    private ImageView image;
    private TextInputLayout tlName;
    private EditText etName, etPrice, etStock;

    public FormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param item Parameter 1.
     * @return A new instance of fragment FormFragment.
     */
    public static FormFragment newInstance(Item item) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = getArguments().getParcelable(ARG_ITEM);
        }

        auth = FirebaseAuth.getInstance();
        handler = FirestoreHandler.getInstance();
        storage = FirebaseStorage.getInstance();

        isEdit = (item != null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form, container, false);

        tlName = view.findViewById(R.id.name_input_text);
        image = view.findViewById(R.id.image);
        etName = view.findViewById(R.id.name_edit_text);
        etPrice = view.findViewById(R.id.price);
        etStock = view.findViewById(R.id.daily_stock);

        Button buttonCancel = view.findViewById(R.id.button_cancel);
        Button buttonDone = view.findViewById(R.id.button_done);
        ImageButton buttonDelete = view.findViewById(R.id.button_delete);

        if (isEdit) {
            etName.setText(item.getName());
            etPrice.setText(String.format("%.2f", item.getPrice()));
            etStock.setText(String.valueOf(item.getDailyStock()));

            StorageReference storageReference = storage.getReference(item.getImagePath());
            GlideApp.with(getContext())
                    .load(storageReference)
                    .into(image);

            buttonDelete.setVisibility(View.VISIBLE);

            buttonDone.setText(R.string.update);
            ((WidgetManager) getContext()).setAppBarTitle(getString(R.string.update_item));
        } else {
            buttonDone.setText(R.string.add);
            ((WidgetManager) getContext()).setAppBarTitle(getString(R.string.add_item));
        }

        etPrice.setOnFocusChangeListener(this);

        image.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonDone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_cancel:
                leave();

                break;

            case R.id.button_done:
                if (!validate()) return;

                if (!isEdit) item = new Item();

                Float price = Float.parseFloat(etPrice.getText().toString());
                int stock = Integer.valueOf(etStock.getText().toString());

                item.setName(etName.getText().toString());
                item.setHawkerId(auth.getCurrentUser().getUid());
                item.setPrice(price);
                item.setDailyStock(stock);
                item.setCurrentStock(stock);

                if (imageUri != null) {
                    uploadImage();
                } else {
                    commitToFirestore();
                }

                break;

            case R.id.button_delete:
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_message)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                storage.getReference()
                                        .child(item.getImagePath())
                                        .delete()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "delete hawker item image:failure", e);
                                            }
                                        });

                                handler.deleteHawkerItem(auth.getCurrentUser().getUid(), item.getId())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                leave();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "delete hawker item:failure", e);
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

                break;

            case R.id.image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE);

                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.price:
                EditText et = (EditText) view;
                String value = et.getText().toString();

                if (!value.isEmpty()) {
                    et.setText(String.format("%.2f", Float.parseFloat(value)));
                }

                break;
        }
    }

    private boolean validate() {
        boolean isValid = true;

        String name = etName.getText().toString();
        String price = etPrice.getText().toString();
        String stock = etStock.getText().toString();

        tlName.setError(null);

        if (name.isEmpty()) {
            tlName.setError(getString(R.string.error_field_empty));
            isValid = false;
        }

        if (price.isEmpty()) {
            etPrice.setError(getString(R.string.error_field_empty));
            isValid = false;
        }

        if (stock.isEmpty()) {
            etStock.setError(getString(R.string.error_field_empty));
            isValid = false;
        }

        if (!isEdit && imageUri == null) {
            Toast.makeText(getContext(), R.string.error_image, Toast.LENGTH_SHORT)
                    .show();
            isValid = false;
        }

        return isValid;
    }

    private void uploadImage() {
        String imagePath;

        if (isEdit) {
            imagePath = item.getImagePath();
        } else {
            String imageId = UUID.randomUUID().toString();

            imagePath = "images/" + imageId;
        }

        item.setImagePath(imagePath);

        StorageReference reference = storage.getReference().child(imagePath);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        commitToFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "upload hawker item image:failure", e);
                    }
                });
    }

    public void commitToFirestore() {
        if (isEdit) {
            handler.updateHawkerItem(auth.getCurrentUser().getUid(), item.getId(), item.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            leave();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "update hawker item:failure", e);
                        }
                    });
        } else {
            handler.addHawkerItem(auth.getCurrentUser().getUid(), item.toMap())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            leave();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "add hawker item:failure", e);
                        }
                    });
        }
    }

    private void leave() {
        ((NavigationHost) getContext()).pop();
    }
}