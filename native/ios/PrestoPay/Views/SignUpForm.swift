import SwiftUI

struct SignUpForm: View {
  @Binding var name: String
  @Binding var username: String
  @Binding var email: String
  @Binding var thumbnail: String
  @Binding var password: String
  @Binding var passwordConfirmation: String
  
  var body: some View {
    VStack(spacing: 16) {
      TextField("Name", text: $name)
        .textFieldStyle(DefaultTextFieldStyle())
        .underline()

      TextField("Username", text: $username)
        .textFieldStyle(DefaultTextFieldStyle())
        .underline()

      TextField("Email", text: $email)
        .textFieldStyle(DefaultTextFieldStyle())
        .underline()

      SecureField("Password", text: $password)
        .textFieldStyle(DefaultTextFieldStyle())
        .underline()

      SecureField("Password(Confirmation)", text: $passwordConfirmation)
        .textFieldStyle(DefaultTextFieldStyle())
        .underline()
    }
  }
}

struct SignUpForm_Previews: PreviewProvider {
  static var previews: some View {
    SignUpForm(
      name: .constant("プレビューユーザー"),
      username: .constant("preview1234"),
      email: .constant("test@calmato.com"),
      thumbnail: .constant(""),
      password: .constant("12345678"),
      passwordConfirmation: .constant("12345678")
    )
  }
}
