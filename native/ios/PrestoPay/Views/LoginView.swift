import SwiftUI

struct LoginView: View {
  @ObservedObject private var loginViewModel = LoginViewModel()

  var body: some View {
    NavigationView {
      ZStack {
        Color.primaryColor
          .edgesIgnoringSafeArea(.all)

        VStack(alignment: .center) {
          Text("Presto Pay")
            .font(.largeTitle)
            .fontWeight(.bold)
            .foregroundColor(.white)

          VStack {
            VStack(spacing: 16) {
              Text(loginViewModel.validationError)
                .font(.caption)
                .foregroundColor(.red)

              LoginForm(
                email: self.$loginViewModel.email,
                password: self.$loginViewModel.password
              )
                .frame(width: 348)

              Button(action: {
                self.loginViewModel.login()
              }) {
                Spacer()
                Text("ログイン")
                Spacer()
              }
                .frame(width: 348, height: 32)
                .foregroundColor(.white)
                .background(Color.primaryColor)

              NavigationLink(destination: LoginView()) {
                Text("パスワードを忘れた方")
                  .font(.system(size: 14))
                  .foregroundColor(Color.primaryColor)
              }
            }

            Divider()
              .padding(.all)

            VStack(spacing: 16) {
              SocialButton(name: "Google", image: "google")
              SocialButton(name: "Twitter", image: "twitter")
              SocialButton(name: "Facebook", image: "facebook")

              NavigationLink(destination: SignUpView()) {
                Text("アカウントをお持ちでない方")
                  .font(.system(size: 14))
                  .foregroundColor(Color.primaryColor)
              }
            }
          }
            .frame(width: 380, height: 460)
            .background(Color.white)
        }
      }
        .navigationBarTitle(Text(""), displayMode: .inline)
        .navigationBarColor(Color.primaryColor)
        .edgesIgnoringSafeArea([.top, .bottom])
    }
  }
}

struct LoginView_Previews: PreviewProvider {
  static var previews: some View {
    LoginView()
  }
}
