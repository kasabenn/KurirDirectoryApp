package com.example.kurirdirectoryapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSearchName: EditText
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var textViewErrorMessage: TextView
    private lateinit var textViewEmptyMessage: TextView
    private lateinit var recyclerViewUsers: RecyclerView

    private lateinit var userAdapter: UserAdapter

    private val daftarPenggunaAsli =
        mutableListOf<UserResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Inisialisasi komponen tampilan
        editTextSearchName =
            findViewById(R.id.editTextSearchName)

        progressBarLoading =
            findViewById(R.id.progressBarLoading)

        textViewErrorMessage =
            findViewById(R.id.textViewErrorMessage)

        textViewEmptyMessage =
            findViewById(R.id.textViewEmptyMessage)

        recyclerViewUsers =
            findViewById(R.id.recyclerViewUsers)

        // Konfigurasi RecyclerView
        recyclerViewUsers.layoutManager =
            LinearLayoutManager(this)

        userAdapter = UserAdapter(emptyList())

        recyclerViewUsers.adapter = userAdapter

        // Mengambil data saat aplikasi dibuka
        muatDataPengguna()

        // Live Search
        editTextSearchName.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val kataKunci =
                        s.toString().trim()

                    filterNamaPengguna(kataKunci)
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun muatDataPengguna() {

        progressBarLoading.visibility = View.VISIBLE
        textViewErrorMessage.visibility = View.GONE
        textViewEmptyMessage.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {

            try {

                val response =
                    ApiClient.apiService.getAllUsers()

                withContext(Dispatchers.Main) {

                    progressBarLoading.visibility =
                        View.GONE

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        val dataDiterima =
                            response.body()!!

                        daftarPenggunaAsli.clear()

                        daftarPenggunaAsli.addAll(
                            dataDiterima
                        )

                        userAdapter.perbaruiDaftar(
                            daftarPenggunaAsli
                        )

                    } else {

                        textViewErrorMessage.text =
                            "Gagal memuat data dari server " +
                                    "(HTTP ${response.code()})"

                        textViewErrorMessage.visibility =
                            View.VISIBLE
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    progressBarLoading.visibility =
                        View.GONE

                    textViewErrorMessage.text =
                        "Koneksi internet bermasalah: " +
                                "${e.localizedMessage ?: "Gagal terhubung"}"

                    textViewErrorMessage.visibility =
                        View.VISIBLE
                }
            }
        }
    }

    private fun filterNamaPengguna(
        kataKunci: String
    ) {

        if (kataKunci.isEmpty()) {

            userAdapter.perbaruiDaftar(
                daftarPenggunaAsli
            )

            textViewEmptyMessage.visibility =
                View.GONE

        } else {

            // Filter berdasarkan NAMA ATAU PERUSAHAAN
            val daftarTersaring =
                daftarPenggunaAsli.filter { pengguna ->

                    pengguna.name.contains(
                        kataKunci,
                        ignoreCase = true
                    ) ||

                            pengguna.company.companyName.contains(
                                kataKunci,
                                ignoreCase = true
                            )
                }

            userAdapter.perbaruiDaftar(
                daftarTersaring
            )

            // Jika tidak ada data
            if (daftarTersaring.isEmpty()) {

                textViewEmptyMessage.text =
                    "Tidak ada kurir yang cocok dengan pencarian Anda"

                textViewEmptyMessage.visibility =
                    View.VISIBLE

            } else {

                textViewEmptyMessage.visibility =
                    View.GONE
            }
        }
    }
}