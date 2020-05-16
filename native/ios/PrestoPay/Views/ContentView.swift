import SwiftUI

struct ContentView: View {
  @State var email: String = ""
  @State var password: String = ""
  
  var body: some View {
    ZStack {
      Color(red: 43/255, green: 172/255, blue: 252/255)
        .edgesIgnoringSafeArea(.all)

      VStack(alignment: .center) {
        Text("Presto Pay")
          .font(.largeTitle)
          .fontWeight(.bold)
          .foregroundColor(Color.white)
        
        VStack(spacing: 16) {
          HStack {
            Image("google")
              .resizable()
              .aspectRatio(contentMode: .fit)
              .frame(height: 28)
            TextField("Email", text: $email)
              .textFieldStyle(RoundedBorderTextFieldStyle())
          }
            .frame(width: 360)
          HStack {
            Image("google")
              .resizable()
              .aspectRatio(contentMode: .fit)
              .frame(height: 28)
            TextField("Password", text: $password)
              .textFieldStyle(RoundedBorderTextFieldStyle())
          }
            .frame(width: 360)
          
          Button("ログイン", action: {})

          Divider()
            .padding(.horizontal)
        }
          .frame(width: 380, height: 560)
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
