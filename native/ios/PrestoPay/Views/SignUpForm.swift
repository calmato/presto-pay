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
      VStack {
        ZStack {
          Color.myBackground
            .edgesIgnoringSafeArea(.all)
          
          Image("photo")
            .resizable()
            .frame(width: 48, height: 48)
            .aspectRatio(contentMode: .fit)
        }
          .frame(width: 120, height: 120)
          .border(Color.gray, width: 1)
        
        Text("[WIP] Select Thumbnail")
      }
      .padding(.bottom, 48)

      CustomTextField(
        text: .constant("Name"),
        value: $name,
        image: .constant("")
      )

      CustomTextField(
        text: .constant("Username"),
        value: $username,
        image: .constant("")
      )
      
      CustomTextField(
        text: .constant("Email"),
        value: $email,
        image: .constant("")
      )

      CustomSecureField(
        text: .constant("Password"),
        value: $password,
        image: .constant("")
      )

      CustomSecureField(
        text: .constant("Password (Confirmation)"),
        value: $passwordConfirmation,
        image: .constant("")
      )
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
