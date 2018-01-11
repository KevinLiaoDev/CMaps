package top.kevinliaodev.cmaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import top.madev.cmapslib.CMapsTools;

public class InputAddressActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_address);
        setTitle("输入地址");
        final EditText editText = findViewById(R.id.et_address);
        findViewById(R.id.btn_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = editText.getText().toString();
                if(TextUtils.isEmpty(address)) return;
                progressDialog.show();
                CMapsTools.queryAddress(InputAddressActivity.this, address, new CMapsTools.OnLocation() {
                    @Override
                    public void location(double lat, double lon) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(InputAddressActivity.this, MapActivity.class);
                        intent.putExtra(MapActivity.CURRENT_LATITUDE, lat);
                        intent.putExtra(MapActivity.CURRENT_LONGITUDE, lon);
                        startActivity(intent);
                    }
                });
            }
        });
        progressDialog = new ProgressDialog(this);
    }

}
