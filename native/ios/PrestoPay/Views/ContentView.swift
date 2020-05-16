import SwiftUI

struct ContentView: View {
  @State var email: String = ""
  @State var password: String = ""

  let primaryColor: Color = Color(red: 43/255, green: 172/255, blue: 252/255)

  var body: some View {
    ZStack {
      primaryColor
        .edgesIgnoringSafeArea(.all)

      VStack(alignment: .center) {
        Text("Presto Pay")
          .font(.largeTitle)
          .fontWeight(.bold)
          .foregroundColor(.white)

        VStack {
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
              TextField("Password", text: $password)
                .textFieldStyle(RoundedBorderTextFieldStyle())
            }
              .frame(width: 348)

            Button(action: {}) {
              Text("ログイン")
            }
              .frame(width: 348, height: 32)
              .foregroundColor(.white)
              .background(primaryColor)

            NavigationLink(destination: ContentView()) {
              Text("パスワードを忘れた方")
                .font(.system(size: 14))
                .foregroundColor(primaryColor)
            }
          }

          Divider()
            .padding(.all)

          VStack(spacing: 16) {
            Button(action: {}) {
              HStack {
                Image("google")
                  .renderingMode(.original)
                  .resizable()
                  .aspectRatio(contentMode: .fit)
                  .frame(height: 24)
                Text("Googleでサインイン")
                  .foregroundColor(.black)
                  .frame(width: 256)
              }
            }
              .frame(width: 348, height: 36)
              .overlay(
                RoundedRectangle(cornerRadius: 3)
                  .stroke(Color.gray, lineWidth: 1)
              )

            Button(action: {}) {
              HStack {
                Image("twitter")
                  .renderingMode(.original)
                  .resizable()
                  .aspectRatio(contentMode: .fit)
                  .frame(height: 24)
                Text("Twitterでサインイン")
                  .foregroundColor(.black)
                  .frame(width: 256)
              }
            }
              .frame(width: 348, height: 36)
              .overlay(
                RoundedRectangle(cornerRadius: 3)
                  .stroke(Color.gray, lineWidth: 1)
              )

            Button(action: {}) {
              HStack {
                Image("facebook")
                  .renderingMode(.original)
                  .resizable()
                  .aspectRatio(contentMode: .fit)
                  .frame(height: 24)
                Text("Facebookでサインイン")
                  .foregroundColor(.black)
                  .frame(width: 256)
              }
            }
              .frame(width: 348, height: 36)
              .overlay(
                RoundedRectangle(cornerRadius: 3)
                  .stroke(Color.gray, lineWidth: 1)
              )

            NavigationLink(destination: ContentView()) {
              Text("アカウントをお持ちでない方")
                .font(.system(size: 14))
                .foregroundColor(primaryColor)
            }
          }
        }
          .frame(width: 380, height: 460)
          .background(Color.white)
      }
    }
  }
}

struct ContentView_Previews: PreviewProvider {
  static var previews: some View {
    ForEach(["iPhone 11", "iPhone SE"], id: \.self) { device in
      ContentView()
        .previewDevice(PreviewDevice(rawValue: device))
    }
  }
}
