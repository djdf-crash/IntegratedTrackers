package com.spybike.integratedtrackers.models

import java.io.Serializable


data class DeviceModel (
    val id: String,
    val accountID: String,
    val nickName: String,
    val unitCode: String
) : Serializable
{
    constructor(): this("","","","")
}

//<row>
//<id>8572</id>
//<account_id>929</account_id>
//<paid_to_date/>
//<nickname>MyBike</nickname>
//<unit_code>358696048760790</unit_code>
//<time_stamp>2017-08-17 17:00:14.0</time_stamp>
//<time_zone>Europe/Kiev</time_zone>
//<sim_id/>
//<response_mode>1</response_mode>
//<response_email/>
//<response_phone>+380963934478</response_phone>
//<mobile_device_type_id/>
//<mobile_device_id/>
//<mobile_device_bluetooth_name/>
//<mobile_device_is_default>0</mobile_device_is_default>
//<user_name>asteryx</user_name>
//<iccid/>
//<balance/>
//<last_charge_date/>
//<tag_active/>
//<tag_date/>
//<monthly_remaining_sms/>
//</row>