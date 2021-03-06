import SwiftUI

struct SignUpView: View {
  @ObservedObject private var signUpViewModel = SignUpViewModel()
  @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
  var body: some View {
      VStack(spacing: 16) {
        Text(signUpViewModel.validationError)
          .font(.caption)
          .foregroundColor(.red)

        SignUpForm(
          name: self.$signUpViewModel.name,
          username: self.$signUpViewModel.username,
          email: self.$signUpViewModel.email,
          thumbnail: self.$signUpViewModel.thumbnail,
          password: self.$signUpViewModel.password,
          passwordConfirmation: self.$signUpViewModel.passwordConfirmation
        )
          .frame(width: 348)
          .padding(.bottom, 16)

        Button(action: {
          self.signUpViewModel.signUp()
        }) {
          Spacer()
          Text("アカウントを作成")
          Spacer()
        }
          .frame(width: 348, height: 32)
          .foregroundColor(.white)
          .background(Color.primaryColor)

        Button(action: {
            self.presentationMode.wrappedValue.dismiss()
        }) {
            Text("既にアカウントをお持ちの方")
                .font(.system(size: 14))
                .foregroundColor(Color.primaryColor)
        }
      }
        // FIXME: ナビゲーションバーに戻るボタンが表示されない
        .navigationBarTitle(Text("Sign Up"), displayMode: .inline)
        .edgesIgnoringSafeArea([.top, .bottom])
        .navigationBarColor(Color.primaryColor)
  }
}

struct SignUpView_Previews: PreviewProvider {
  static var previews: some View {
    SignUpView()
  }
}
