import SwiftUI

struct LoginForm: View {
  @Binding var email: String
  @Binding var password: String

  var body: some View {
    VStack(spacing: 16) {
      CustomTextField(
        text: .constant("Email"),
        value: $email,
        image: .constant("email")
      )

      CustomSecureField(
        text: .constant("Password"),
        value: $password,
        image: .constant("password")
      )
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
