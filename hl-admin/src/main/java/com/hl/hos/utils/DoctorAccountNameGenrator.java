package com.hl.hos.utils;

import com.hl.hos.pojo.Doctor_info;

import java.util.List;

/**
 * @author JaneOnly
 * @date 2022年02月09日 17:42
 */
public class DoctorAccountNameGenrator {

    public static int getMaxNum(List<Doctor_info> hos_doctors){
        int accountMaxNum = 0;
        //获取医生账号最大尾数
        for (int i = 0; i < hos_doctors.size(); i++) {
            int charDe= 0;
            //获取医生的尾数
            Doctor_info df = hos_doctors.get(i);
            String doctor_account = df.getDoctor_account();
            if(doctor_account.contains("E")){
                charDe = doctor_account.indexOf("E");
            }
            if(doctor_account.contains("D")){
                charDe = doctor_account.indexOf("D");
            }
            int comparNum = Integer.parseInt(doctor_account.substring(charDe + 1));
            if(accountMaxNum <comparNum){
                accountMaxNum = comparNum;
            }
        }
        return accountMaxNum;
    }
}
