import SwiftUI

struct LoginView: View {
  @State private var email: String = ""
  @State private var password: String = ""

  var body: some View {
    ZStack {
      PrimaryColor
        .edgesIgnoringSafeArea(.all)

      VStack(alignment: .center) {
        Text("Presto Pay")
          .font(.largeTitle)
          .fontWeight(.bold)
          .foregroundColor(.white)

        VStack {
          VStack(spacing: 16) {
            LoginForm(email: self.$email, password: self.$password)

            NavigationLink(destination: LoginView()) {
              Text("パスワードを忘れた方")
                .font(.system(size: 14))
                .foregroundColor(PrimaryColor)
            }
          }

          Divider()
            .padding(.all)

          VStack(spacing: 16) {
            SocialButton(name: "Google", image: "google")
            SocialButton(name: "Twitter", image: "twitter")
            SocialButton(name: "Facebook", image: "facebook")

            NavigationLink(destination: LoginView()) {
              Text("アカウントをお持ちでない方")
                .font(.system(size: 14))
                .foregroundColor(PrimaryColor)
            }
          }
        }
          .frame(width: 380, height: 460)
          .background(Color.white)
      }
    }
  }
}

struct LoginView_Previews: PreviewProvider {
  static var previews: some View {
    ForEach(["iPhone 11", "iPhone SE"], id: \.self) { device in
      LoginView()
        .previewDevice(PreviewDevice(rawValue: device))
    }
  }
}
