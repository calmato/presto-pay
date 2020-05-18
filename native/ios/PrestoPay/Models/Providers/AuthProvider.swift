import Foundation
import Combine

struct APIError: Error {
  var description: String
}

final class AuthProvider: AuthProviderProtocol {
  private var cancellble: AnyCancellable?
  func login(email: String, password: String) -> AnyPublisher<User, Error> {
    let url = URL(string: "\(baseURL)/v1/users/check-email")! // TODO: リクエスト投げられるかチェック用
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")

    do {
      let form: CheckEmailForm = CheckEmailForm(email: email)
      request.httpBody = try JSONEncoder().encode(form)
    } catch {
      return error as! AnyPublisher<User, Error>
    }

    let result = URLSession.shared.dataTaskPublisher(for: request)
      .tryMap({ data, response -> Data in
        guard let httpRes = response as? HTTPURLResponse else {
            throw APIError(description: "http response not found")
        }
        
        if (200..<300).contains(httpRes.statusCode) == false {
            throw APIError(description: "Bad Http Status Code")
        }
        
        return data
      })
      .decode(type: User.self, decoder: JSONDecoder())
      .eraseToAnyPublisher()
    
    return result
  }
}
