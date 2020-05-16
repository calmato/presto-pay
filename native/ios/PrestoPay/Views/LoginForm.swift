//
//  LoginForm.swift
//  PrestoPay
//
//  Created by 西川　直志 on 2020/05/17.
//  Copyright © 2020 calmato. All rights reserved.
//

import SwiftUI

struct LoginForm: View {
  @Binding var email: String
  @Binding var password: String
  
  var body: some View {
    VStack(spacing: 16) {
      HStack {
        Image("mail")
          .resizable()
          .aspectRatio(contentMode: .fit)
          .frame(height: 28)
        TextField("Email", text: $email)
          .textFieldStyle(RoundedBorderTextFieldStyle())
      }
        .frame(width: 348)
      
      HStack {
        Image("password")
          .resizable()
          .aspectRatio(contentMode: .fit)
          .frame(height: 28)
        SecureField("Password", text: $password)
          .textFieldStyle(RoundedBorderTextFieldStyle())
      }
        .frame(width: 348)

      Button(action: {}) {
        Text("ログイン")
      }
        .frame(width: 348, height: 32)
        .foregroundColor(.white)
        .background(PrimaryColor)
    }
  }
}

struct LoginForm_Previews: PreviewProvider {
  static var previews: some View {
    LoginForm(
      email: .constant("test@calmato.com"),
      password: .constant("12345678")
    )
  }
}
