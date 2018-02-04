/*
 * <!--
 *   ~ Copyright (c) 2017. ThanksMister LLC
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License. 
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~ http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software distributed 
 *   ~ under the License is distributed on an "AS IS" BASIS, 
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *   ~ See the License for the specific language governing permissions and 
 *   ~ limitations under the License.
 *   -->
 */

package com.thanksmister.iot.mqtt.alarmpanel.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.thanksmister.iot.mqtt.alarmpanel.BaseActivity
import com.thanksmister.iot.mqtt.alarmpanel.R
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_STATE_TOPIC
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_BROKER
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_CLIENT_ID
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_COMMAND_TOPIC
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_PASSWORD
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_PORT
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_TLS_CONNECTION
import com.thanksmister.iot.mqtt.alarmpanel.network.MQTTOptions.Companion.PREF_USERNAME
import com.thanksmister.iot.mqtt.alarmpanel.ui.Configuration
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MqttSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject lateinit var configuration: Configuration

    private var brokerPreference: EditTextPreference? = null
    private var clientPreference: EditTextPreference? = null
    private var portPreference: EditTextPreference? = null
    private var commandTopicPreference: EditTextPreference? = null
    private var stateTopicPreference: EditTextPreference? = null
    private var userNamePreference: EditTextPreference? = null
    private var sslPreference: CheckBoxPreference? = null
    private var passwordPreference: EditTextPreference? = null
    private var mqttOptions: MQTTOptions? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey : String?) {
        addPreferencesFromResource(R.xml.mqtt_preferences)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        brokerPreference = findPreference(PREF_BROKER) as EditTextPreference
        clientPreference = findPreference(PREF_CLIENT_ID) as EditTextPreference
        portPreference = findPreference(PREF_PORT) as EditTextPreference
        commandTopicPreference = findPreference(PREF_COMMAND_TOPIC) as EditTextPreference
        stateTopicPreference = findPreference(PREF_STATE_TOPIC) as EditTextPreference
        userNamePreference = findPreference(PREF_USERNAME) as EditTextPreference
        passwordPreference = findPreference(PREF_PASSWORD) as EditTextPreference
        sslPreference = findPreference(PREF_TLS_CONNECTION) as CheckBoxPreference

        if (isAdded && activity != null) {
            mqttOptions = (activity as BaseActivity).readMqttOptions()
        }

        brokerPreference!!.text = mqttOptions!!.getBroker()
        clientPreference!!.text = mqttOptions!!.getClientId().toString()
        portPreference!!.text = mqttOptions!!.getPort().toString()
        commandTopicPreference!!.text = mqttOptions!!.getCommandTopic()
        stateTopicPreference!!.text = mqttOptions!!.getStateTopic()
        userNamePreference!!.text = mqttOptions!!.getUsername()
        passwordPreference!!.text = mqttOptions!!.getPassword()
        sslPreference!!.isChecked = mqttOptions!!.getTlsConnection()

        if (!TextUtils.isEmpty(mqttOptions!!.getBroker())) {
            brokerPreference!!.summary = mqttOptions!!.getBroker()
        }
        if (!TextUtils.isEmpty(mqttOptions!!.getClientId())) {
            clientPreference!!.summary = mqttOptions!!.getClientId()
        }
        if (!TextUtils.isEmpty(mqttOptions!!.getPort().toString())) {
            portPreference!!.summary = mqttOptions!!.getPort().toString()
        }
        if (!TextUtils.isEmpty(mqttOptions!!.getCommandTopic())) {
            commandTopicPreference!!.summary = mqttOptions!!.getCommandTopic()
        }
        if (!TextUtils.isEmpty(mqttOptions!!.getStateTopic())) {
            stateTopicPreference!!.summary = mqttOptions!!.getStateTopic()
        }
        if (!TextUtils.isEmpty(mqttOptions!!.getUsername())) {
            userNamePreference!!.summary = mqttOptions!!.getUsername()
        }
        if (!TextUtils.isEmpty(mqttOptions!!.getPassword())) {
            passwordPreference!!.summary = toStars(mqttOptions!!.getPassword())
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val value: String
        when (key) {
            PREF_BROKER -> {
                value = brokerPreference!!.text
                if (!TextUtils.isEmpty(value)) {
                    mqttOptions!!.setBroker(value)
                    brokerPreference!!.summary = value
                } else if (isAdded) {
                    Toast.makeText(activity, R.string.text_error_blank_entry, Toast.LENGTH_LONG).show()
                }
            }
            PREF_CLIENT_ID -> {
                value = clientPreference!!.text
                if (!TextUtils.isEmpty(value)) {
                    mqttOptions!!.setClientId(value)
                    clientPreference!!.summary = value
                } else if (isAdded) {
                    Toast.makeText(activity, R.string.text_error_blank_entry, Toast.LENGTH_LONG).show()
                    clientPreference!!.text = mqttOptions!!.getClientId()
                }
            }
            PREF_PORT -> {
                value = portPreference!!.text
                if (value.matches("[0-9]+".toRegex()) && !TextUtils.isEmpty(value)) {
                    mqttOptions!!.setPort(Integer.valueOf(value)!!)
                    portPreference!!.summary = value.toString()
                } else if (isAdded) {
                    Toast.makeText(activity, R.string.text_error_only_numbers, Toast.LENGTH_LONG).show()
                    portPreference!!.text = mqttOptions!!.getPort().toString()
                }
            }
            PREF_COMMAND_TOPIC -> {
                value = commandTopicPreference!!.text
                if (!TextUtils.isEmpty(value)) {
                    mqttOptions!!.setCommandTopic(value)
                    commandTopicPreference!!.summary = value
                } else if (isAdded) {
                    Toast.makeText(activity, R.string.text_error_blank_entry, Toast.LENGTH_LONG).show()
                    commandTopicPreference!!.text = mqttOptions!!.getCommandTopic()
                }
            }
            PREF_STATE_TOPIC -> {
                value = stateTopicPreference!!.text
                if (!TextUtils.isEmpty(value)) {
                    mqttOptions!!.setAlarmTopic(value)
                    stateTopicPreference!!.summary = value
                } else if (isAdded) {
                    Toast.makeText(activity, R.string.text_error_blank_entry, Toast.LENGTH_LONG).show()
                    stateTopicPreference!!.text = mqttOptions!!.getStateTopic()
                }
            }
            PREF_USERNAME -> {
                value = userNamePreference!!.text
                mqttOptions!!.setUsername(value)
                userNamePreference!!.summary = value
            }
            PREF_PASSWORD -> {
                value = passwordPreference!!.text
                mqttOptions!!.setPassword(value)
                passwordPreference!!.summary = toStars(value)
            }
            PREF_TLS_CONNECTION -> {
                val checked = sslPreference!!.isChecked
                mqttOptions!!.setTlsConnection(checked)
            }
        }
    }

    private fun toStars(textToStars: String?): String {
        var text = textToStars
        val sb = StringBuilder()
        for (i in 0 until text!!.length) {
            sb.append('*')
        }
        text = sb.toString()
        return text
    }
}