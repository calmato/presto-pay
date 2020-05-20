import SwiftUI

struct SignUpView: View {
  @ObservedObject private var signUpViewModel = SignUpViewModel()

  var body: some View {
    NavigationView {
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
          .background(Color.myPrimary)

//        NavigationLink(destination: LoginView()) {
//          Text("既にアカウントをお持ちの方")
//            .font(.system(size: 14))
//            .foregroundColor(Color.myPrimary)
//        }
      }
        .navigationBarTitle(Text("Sign Up"), displayMode: .inline)
        .navigationBarBackButtonHidden(false)
        .navigationBarColor(Color.myPrimary)
    }
  }
}

struct SignUpView_Previews: PreviewProvider {
  static var previews: some View {
    SignUpView()
  }
}
