package com.febrian.whatsappclone.ui.finduser

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.febrian.whatsappclone.data.User
import com.febrian.whatsappclone.databinding.ActivityFindUserBinding
import com.google.firebase.database.*


class FindUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindUserBinding
    private lateinit var listUser: ArrayList<User>
    private lateinit var contactList: ArrayList<User>
    private lateinit var userAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listUser = ArrayList()
        contactList = ArrayList()
        userAdapter = UserListAdapter(listUser)

        getContactList()
        initializeRecyclerView()

    }

    @SuppressLint("Range")
    private fun getContactList() {

        val iSOPrefix: String = getCountryISO()

        val phones: Cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        ) ?: return

        while (phones.moveToNext()) {
            val name: String =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            var phone: String =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (phone.elementAt(0).toString() != "+")
                phone = iSOPrefix + phone;

            val mContact = User("",name, phone)
            contactList.add(mContact);
            getUserDetails(mContact)
        }
    }

    private fun getUserDetails(mContact: User) {
        val mUserDB: DatabaseReference = FirebaseDatabase.getInstance().reference.child("user")
        val query: Query = mUserDB.orderByChild("phone").equalTo(mContact.phone)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var phone = ""
                    val name = ""
                    for (childSnapshot in dataSnapshot.children) {
                        if (childSnapshot.child("phone").value != null) phone =
                            childSnapshot.child("phone").value.toString()
                        if (childSnapshot.child("name").value != null) phone =
                            childSnapshot.child("name").value.toString()
                        val mUser = User("",name, phone)

                        for (mContactIterator in contactList) {
                            if (mContactIterator.phone == mUser.phone) {
                                mUser.name = mContactIterator.name
                            }
                        }

                        Log.d("NAME", mUser.name)

                        listUser.add(mUser)
                        userAdapter.notifyDataSetChanged()
                        return
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getCountryISO(): String {
        var iso: String? = null
        val telephonyManager =
            applicationContext.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (telephonyManager.networkCountryIso != null) if (telephonyManager.networkCountryIso.toString() != "") iso =
            telephonyManager.networkCountryIso.toString()
        return CountryToPhonePrefix.getPhone(iso.toString()).toString()
    }

    private fun initializeRecyclerView() {
        binding.rv.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            adapter = userAdapter
        }
    }
}