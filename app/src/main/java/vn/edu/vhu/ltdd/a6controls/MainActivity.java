package vn.edu.vhu.ltdd.a6controls;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // TODO: thay 2201234567 bằng MSSV của bạn
    private static final String TAG = "A6_2201234567";

    public static final String EXTRA_TOM_TAT = "extra_tom_tat";

    private EditText edtHoTen, edtMssv;
    private Spinner spMonHoc;
    private RadioGroup rgHeDaoTao;
    private CheckBox cbSang, cbChieu, cbToi;
    private MaterialSwitch swThongBao;
    private ToggleButton tgUuTien;

    // ==========================================
    // BỔ SUNG: Activity Result API (Lab A5)
    // Nhận dữ liệu/yêu cầu sửa từ màn hình Xác nhận
    // ==========================================
    private final ActivityResultLauncher<Intent> confirmLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String editMsg = result.getData().getStringExtra("EDIT_MESSAGE");
                    if (editMsg != null) {
                        Toast.makeText(this, editMsg, Toast.LENGTH_LONG).show();
                        edtHoTen.requestFocus();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        edtHoTen = findViewById(R.id.edtHoTen);
        edtMssv = findViewById(R.id.edtMssv);
        spMonHoc = findViewById(R.id.spMonHoc);
        rgHeDaoTao = findViewById(R.id.rgHeDaoTao);
        cbSang = findViewById(R.id.cbSang);
        cbChieu = findViewById(R.id.cbChieu);
        cbToi = findViewById(R.id.cbToi);
        swThongBao = findViewById(R.id.swThongBao);
        tgUuTien = findViewById(R.id.tgUuTien);
        Button btnXacNhan = findViewById(R.id.btnXacNhan);
        Button btnLamLai = findViewById(R.id.btnLamLai);

        // ---- Spinner: đổ dữ liệu từ res/values/arrays.xml bằng ArrayAdapter ----
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.mon_hoc, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonHoc.setAdapter(adapter);

        spMonHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Đã chọn học phần: " + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không dùng, nhưng vẫn phải cài đặt vì đây là phương thức của interface
            }
        });

        // ---- CheckBox: lắng nghe thay đổi ----
        cbToi.setOnCheckedChangeListener((buttonView, isChecked) ->
                Log.d(TAG, "Buổi Tối: " + (isChecked ? "chọn" : "bỏ chọn")));

        // ---- RadioGroup: chỉ chọn được một ----
        rgHeDaoTao.setOnCheckedChangeListener((group, checkedId) -> {
            String he = (checkedId == R.id.rbChinhQuy) ? getString(R.string.he_chinh_quy)
                    : getString(R.string.he_vlvh);
            Log.d(TAG, "Hệ đào tạo: " + he);
        });

        // ---- Switch ----
        swThongBao.setOnCheckedChangeListener((buttonView, isChecked) ->
                Log.d(TAG, "Nhận thông báo: " + isChecked));

        // BỔ SUNG: Switch "Chế độ tối" đổi nền màn hình ngay lập tức
        MaterialSwitch swDarkMode = findViewById(R.id.swDarkMode);
        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        btnXacNhan.setOnClickListener(v -> xacNhan());
        btnLamLai.setOnClickListener(v -> lamLai());
    }

    /** Kiểm tra dữ liệu rồi chuyển sang màn hình xác nhận. */
    private void xacNhan() {
        String hoTen = edtHoTen.getText().toString().trim();
        String mssv = edtMssv.getText().toString().trim();

        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            edtHoTen.requestFocus();
            return;
        }
        if (mssv.length() != 10) {
            edtMssv.setError(getString(R.string.err_mssv));
            edtMssv.requestFocus();
            return;
        }
        if (rgHeDaoTao.getCheckedRadioButtonId() == -1) {   // -1 = chưa chọn nút nào
            Toast.makeText(this, R.string.err_he, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> buoiHoc = new ArrayList<>();
        if (cbSang.isChecked()) buoiHoc.add(getString(R.string.buoi_sang));
        if (cbChieu.isChecked()) buoiHoc.add(getString(R.string.buoi_chieu));
        if (cbToi.isChecked()) buoiHoc.add(getString(R.string.buoi_toi));
        if (buoiHoc.isEmpty()) {
            Toast.makeText(this, R.string.err_buoi, Toast.LENGTH_SHORT).show();
            return;
        }

        String he = (rgHeDaoTao.getCheckedRadioButtonId() == R.id.rbChinhQuy)
                ? getString(R.string.he_chinh_quy) : getString(R.string.he_vlvh);

        String tomTat = getString(R.string.tom_tat_format,
                hoTen,
                mssv,
                spMonHoc.getSelectedItem().toString(),
                he,
                TextUtils.join(", ", buoiHoc),  // String.join() cần API 26 nên dùng TextUtils
                swThongBao.isChecked() ? getString(R.string.co) : getString(R.string.khong),
                tgUuTien.isChecked() ? getString(R.string.bat) : getString(R.string.tat));

        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra(EXTRA_TOM_TAT, tomTat);
        // BỔ SUNG: Dùng Activity Result API (Lab A5) thay vì startActivity đơn thuần
        // để nhận kết quả trả về khi người dùng bấm nút Sửa ở màn hình Xác nhận
        confirmLauncher.launch(intent);
    }

    /** Đưa mọi control về trạng thái ban đầu. */
    private void lamLai() {
        edtHoTen.setText("");
        edtMssv.setText("");
        edtHoTen.setError(null);
        edtMssv.setError(null);
        spMonHoc.setSelection(0);
        rgHeDaoTao.clearCheck();
        cbSang.setChecked(false);
        cbChieu.setChecked(false);
        cbToi.setChecked(false);
        swThongBao.setChecked(true);
        tgUuTien.setChecked(false);
        edtHoTen.requestFocus();
    }
}

